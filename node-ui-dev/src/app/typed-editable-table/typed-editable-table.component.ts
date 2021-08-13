import { AfterContentInit, Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormControl, Validators } from '@angular/forms';

import { CommonUtilsService } from '../services';

@Component({
  selector: 'app-typed-editable-table',
  templateUrl: './typed-editable-table.component.html',
  styleUrls: ['./typed-editable-table.component.scss']
})
export class TypedEditableTableComponent implements AfterContentInit {
  nonEmptyFormControl = new FormControl('', [
    Validators.required
  ]);

  displayedColumns: string[] = [ 'name', 'value' ];

  @Input("datasource") datasource: any = [];

  @Input("nameHeader") nameHeader: string = '';
  @Input("valueHeader") valueHeader: string = '';

  @Input("nameFieldName") nameFieldName : string = '';
  @Input("valueFieldName") valueFieldName: string = '';
  @Input("defaultValueFieldName") defaultValueFieldName: string = '';

  @Input("selectedRowGetter") selectedRowGetter: () => number = () => -1;
  @Input("selectedRowSetter") selectedRowSetter: (selectedRow: number) => void = (selectedRow: number) => {};

  @Input("typeDefGetter") typeDefGetter: (rowObject: any) => any = (rowObject: any) => {};

  @Output("editingRow") editingRowEvent: EventEmitter<number> = new EventEmitter<number>();

  editingRow: number = -1;

  set setEditingRow(editingRow: number) {
    this.editingRow = editingRow;
    this.editingRowEvent.emit(this.editingRow);
  }

  editingName: string = '';
  editingDefaultValue: string = '';

  constructor(public commonUtilsService: CommonUtilsService) { }

  ngAfterContentInit (): void {
    this.setDefaults();
  }

  getRow(rowObject: any) {
    for (let i: number = 0; i < this.datasource.length; i++) {
      if (rowObject === this.datasource[i]) {
        return i;
      }
    }
    return -1;
  }

  get selected(): number {
    return this.selectedRowGetter();
  }

  set selected(row: number) {
    this.selectedRowSetter(row);
  }

  setDefault(rowObject: any): void {
    rowObject[this.valueFieldName] = rowObject[this.defaultValueFieldName];
  }

  setDefaults(): void {
    this.datasource.forEach((rowObject: any) => this.setDefault(rowObject));
  }

  cancelEditing(rowObject: any): void {
    const editingRow: number = this.getRow(rowObject);
    if (this.editingRow !== editingRow) {
      this.editingRow = -1;
    }
    this.commonUtilsService.fixMaterialBug();
  }

  toggleEditing(rowObject: any): void {
    const editingRow: number = this.getRow(rowObject);
    if (this.editingRow === editingRow) {
      this.editingRow = -1;
    } else {
      this.editingRow = editingRow;
      this.editingName = rowObject[this.nameFieldName];
      this.editingDefaultValue = rowObject[this.defaultValueFieldName];
    }
    this.commonUtilsService.fixMaterialBug();
  }

  saveEditing(rowObject: any): void {
    rowObject[this.nameFieldName] = !this.editingName || this.editingName === '' ? rowObject[this.nameFieldName] : this.editingName;
    rowObject[this.defaultValueFieldName] = this.editingDefaultValue
    this.editingRow = -1;
    this.commonUtilsService.fixMaterialBug();
  }
}
