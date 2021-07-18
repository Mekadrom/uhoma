import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';

@Component({
  selector: 'app-typed-editable-table',
  templateUrl: './typed-editable-table.component.html',
  styleUrls: ['./typed-editable-table.component.css']
})
export class TypedEditableTableComponent implements OnInit {
  displayedColumns: string[] = [ 'name', 'value' ];

  datasource: any[] = [];

  nameHeader: string = 'Name';
  valueHeader: string = 'Value';

  valueGetter: (rowObject: any) => string | null = (rowObject: any) => '';
  valueSetter: (rowObject: any, value: string | null) => void = (rowObject: any, value: string | null) => {};

  objectTypeDefGetter: (rowObject: any) => any = (rowObject: any) => {};

  defaulter: (rowObject: any) => void = (rowObject: any) => {};

  getSelectedRow: () => number = () => -1;

  setSelectedRow: (selectedRow: any) => void = (selectedRow: any) => {};

  @Input('datasource')
  public set setDatasource(datasource: any[]) {
    this.datasource = datasource;
  }

  @Input('nameHeader')
  public set setNameHeader(nameHeader: string) {
    this.nameHeader = nameHeader;
  }

  @Input('valueHeader')
  public set setValueHeader(valueHeader: string) {
    this.valueHeader = valueHeader;
  }

  @Input('getValue')
  public set setValueGetter(valueGetter: (rowObject: any) => string | null) {
    this.valueGetter = valueGetter;
  }

  @Input('setValue')
  public set setValueSetter(valueSetter: (rowObject: any, value: string | null) => void) {
    this.valueSetter = valueSetter;
  }

  @Input('objectTypeDefGetter')
  public set setObjectTypeDefGetter(objectTypeDefGetter: (rowObject: any) => any) {
    this.objectTypeDefGetter = objectTypeDefGetter;
  }

  @Input('defaultSetter')
  public set setDefaulter(defaulter: (rowObject: any) => void) {
    this.defaulter = defaulter;
  }

  @Input('selectedRowGetter')
  public set setSelectedRowGetter(selectedRowGetter: () => number) {
    this.getSelectedRow = selectedRowGetter;
  }

  @Input('selectedRowSetter')
  public set setSelectedRowSetter(selectedRowSetter: (selectedRow: any) => void) {
    this.setSelectedRow = selectedRowSetter;
  }

  @Output('selectedRowEmitter') selectedRowEmitter = new EventEmitter<any>();

  constructor() { }

  ngOnInit(): void {
  }

  getValue(rowObject: any): string | null {
    return this.valueGetter(rowObject);
  }

  setValue(rowObject: any, event: any): void {
    this.valueSetter(rowObject, event);
  }

  isStringField(rowObject: any): boolean {
    return this.objectTypeDefGetter(rowObject).type === 'string';
  }

  isNumberField(rowObject: any): boolean {
    return this.objectTypeDefGetter(rowObject).type === 'number';
  }

  isListField(rowObject: any): boolean {
    return this.objectTypeDefGetter(rowObject).type === 'list';
  }

  highlight(rowNum: number): void {
    if (this.getSelectedRow() === rowNum) {
      this.selectedRowEmitter.emit(-1);
      this.setSelectedRow(-1);
    } else {
      this.selectedRowEmitter.emit(rowNum);
      this.setSelectedRow(rowNum);
    }
  }

  getListValues(rowObject: any): string[] {
    return this.objectTypeDefGetter(rowObject).values;
  }

  listValueAllowsEmpty(rowObject: any) {
    const config: any = this.objectTypeDefGetter(rowObject)?.config;
    if (config) {
      return config.allowsEmpty;
    }
    return true;
  }

  consumeEvent(event: any): void {
    event?.stopPropagation();
  }

  omitSpecialChar(event: any) {
    let k = event.charCode;
    return ((k > 64 && k < 91) || (k > 96 && k < 123) || k == 8 || k == 32 || (k >= 48 && k <= 57));
  }

  onlyNumeric(event: any) {
    let k = event.charCode;
    return k >= 48 && k <= 57;
  }
}
