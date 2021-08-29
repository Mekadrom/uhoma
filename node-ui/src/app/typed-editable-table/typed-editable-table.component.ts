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

  @Input("typeDefGetter") typeDefGetter: (rowObject: any) => any = (rowObject: any) => {};

  @Output("selectedRow") selectedRowEvent: EventEmitter<number> = new EventEmitter<number>();
  @Output("editingRow") editingRowEvent: EventEmitter<number> = new EventEmitter<number>();

  selectedRowValue: number = -1;
  editingRowValue: number = -1;

  get selectedRow(): number {
    return this.selectedRowValue;
  }

  set selectedRow(selectedRowValue: number) {
    this.selectedRowValue = selectedRowValue;
    this.selectedRowEvent.emit(this.selectedRowValue);
  }

  get editingRow(): number {
    return this.editingRowValue;
  }

  set editingRow(editingRowValue: number) {
    this.editingRowValue = editingRowValue;
    this.editingRowEvent.emit(this.editingRowValue);
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
