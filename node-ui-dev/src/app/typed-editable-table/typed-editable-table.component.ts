import { Component, OnInit, Input } from '@angular/core';
import { CommonUtilsService } from '../services';

@Component({
  selector: 'app-typed-editable-table',
  templateUrl: './typed-editable-table.component.html',
  styleUrls: ['./typed-editable-table.component.scss']
})
export class TypedEditableTableComponent implements OnInit {
  displayedColumns: string[] = [ 'name', 'value' ];

  datasource: any = [];

  nameHeader: string = '';
  valueHeader: string = '';

  nameFieldName : string = '';
  valueFieldName: string = '';

  selectedRowGetter: () => number = () => -1;
  selectedRowSetter: (selectedRow: number) => void = (selectedRow: number) => {};

  typeDefGetter: (rowObject: any) => any = (rowObject: any) => {};

  defaultSetter: (rowObject: any) => void = (rowObject: any) => {};

  editingRow: number = -1;

  @Input("datasource")
  public set setDatasource(datasource: any) {
    this.datasource = datasource;
  }

  @Input("nameHeader")
  public set setNameHeader(nameHeader: string) {
    this.nameHeader = nameHeader;
  }

  @Input("valueHeader")
  public set setValueHeader(valueHeader: string) {
    this.valueHeader = valueHeader;
  }

  @Input("nameFieldName")
  public set setNameFieldName(nameFieldName: string) {
    this.nameFieldName = nameFieldName;
  }

  @Input("valueFieldName")
  public set setValueFieldName(valueFieldName: string) {
    this.valueFieldName = valueFieldName;
  }

  @Input("selectedRowGetter")
  public set setSelectedRowGetter(selectedRowGetter: () => number) {
    this.selectedRowGetter = selectedRowGetter;
  }

  @Input("selectedRowSetter")
  public set setSelectedRowSetter(selectedRowSetter: (selectedRow: number) => void) {
    this.selectedRowSetter = selectedRowSetter;
  }

  @Input("typeDefGetter")
  public set setTypeDefGetter(typeDefGetter: (rowObject: any) => any) {
    this.typeDefGetter = typeDefGetter;
  }

  @Input("defaultSetter")
  public set setDefaultSetter(defaultSetter: (rowObject: any) => void) {
    this.defaultSetter = defaultSetter;
  }

  constructor(public commonUtilsService: CommonUtilsService) { }

  ngOnInit(): void { }

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
}
