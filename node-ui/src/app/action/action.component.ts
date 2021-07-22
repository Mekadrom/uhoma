import { Component, OnInit, Input } from '@angular/core';

import { ToastrService } from 'ngx-toastr';

import { ListedParameter } from './listed-parameter';

import { NodeAction } from '../models/node-action';
import { ActionParameter } from '../models/action-parameter';
import { ActionParameterType } from '../models/action-parameter-type';

import { NodeService } from '../services/node.service';
import { WebSocketService } from '../services/web-socket.service';

@Component({
  selector: 'app-action',
  templateUrl: './action.component.html',
  styleUrls: ['./action.component.css']
})
export class ActionComponent implements OnInit {
  displayedColumns: string[] = [ 'name', 'currentValue' ];

  nameHeader: string = 'Parameter Name';
  valueHeader: string = 'Value';

  selectedRow: number = -1;

  nodeAction?: NodeAction;

  parameters: ListedParameter[] = [];

  actionParameterTypes: ActionParameterType[] = [];

  newParameterType?: ActionParameterType;

  newParameterName: string = '';

  newParameterDefaultValue?: string;

  @Input('setAction')
  public set setAction(nodeAction: NodeAction | undefined) {
    this.nodeAction = nodeAction;
    this.refresh();
  }

  constructor(private webSocket: WebSocketService,
              private nodeService: NodeService,
              private toastr: ToastrService) { }

  refresh(): void {
    this.newParameterType = this.actionParameterTypes[0];
    this.newParameterName = '';
    this.newParameterDefaultValue = '';
    this.parameters = [];
    if (this.nodeAction) {
      for (let i = 0; i < this.nodeAction?.parameters.length; i++) {
        this.parameters.push({ rowNum: i, currentValue: this.nodeAction.parameters[i].defaultValue, actionParameter: this.nodeAction.parameters[i] })
      }
      this.parameters.forEach(param => this.setDefaultValue(param));
      this.parameters.sort((a: ActionParameter, b: ActionParameter) => (!a.actionParameterSeq || !b.actionParameterSeq) ? 0 : (a.actionParameterSeq > b.actionParameterSeq) ? 1 : -1);
      this.parameters = this.parameters.slice();
    }
    this.selectedRow = -1;
  }

  setDefaultValue(param: ListedParameter): void {
    param.currentValue = param.actionParameter.defaultValue;
    if (!param.currentValue) {
      if (this.isListTypeSelected(param.actionParameter.actionParameterType)) {
        const config: any = this.getListConfig(param.actionParameter.actionParameterType);
        if (config) {
          param.currentValue = config.defaultListValue;
        }
      }
    }
  }

  getListConfig(actionParameterType?: ActionParameterType): any {
    if(!actionParameterType || !actionParameterType.typeDef) {
      return {};
    }
    return JSON.parse(actionParameterType?.typeDef)?.config;
  }

  fetchActionParameterTypes(): void {
    this.nodeService.getActionParameterTypes()
    .subscribe(
      (data: ActionParameterType[]) => {
        this.actionParameterTypes = data;
        this.refresh();
      }
    );
  }

  ngOnInit(): void {
    this.fetchActionParameterTypes();
  }

  runActionWithParams(): void {
    this.webSocket.sendAction(this.nodeAction);
  }

  add(): void {
    if (!this.newParameterType) {
      this.toastr.error('Please select a type.');
      return;
    }
    if (this.newParameterName === '') {
      this.toastr.error('Please input a name.');
      return;
    }
    if (this.nodeAction?.parameters.map(param => param.name).some((paramName) => paramName?.toUpperCase() === this.newParameterName.toUpperCase())) {
      this.toastr.error('A parameter with that name already exists for this action. Please choose another name.')
      return;
    }
    const param: ActionParameter = {
      name: this.newParameterName,
      defaultValue: this.newParameterDefaultValue,
      actionParameterType: this.newParameterType
    };
    const listedParam: ListedParameter = {
      currentValue: this.newParameterDefaultValue,
      actionParameter: param
    };
    this.setDefault(param);
    this.parameters.push(listedParam);
    this.nodeAction?.parameters.push(param);
    this.refresh();
  }

  removeSelected(): void {
    if (this.selectedRow !== -1) {
      if (this.nodeAction && this.nodeAction.parameters) {
        this.removeParam(this.nodeAction.parameters, this.selectedRow);
      }
      if (this.parameters) {
        this.removeParam(this.parameters, this.selectedRow);
      }
    }
    this.refresh();
  }

  removeParam(params: ActionParameter[], index: number): void {
    for (let i: number = 0; i < params.length; i++) {
      const param = params[i];
      if (param) {
        if (i == index) {
          params.splice(i, 1);
        } else if (i > index && param.rowNum) {
          param.rowNum = param.rowNum - 1;
        }
      }
    }
  }

  isStringTypeSelected(actionParameterType?: ActionParameterType): boolean {
    if(!actionParameterType || !actionParameterType.typeDef) {
      return false;
    }
    return JSON.parse(actionParameterType?.typeDef).type === 'string';
  }

  isNumberTypeSelected(actionParameterType?: ActionParameterType): boolean {
    if(!actionParameterType || !actionParameterType.typeDef) {
      return false;
    }
    return JSON.parse(actionParameterType?.typeDef).type === 'number';
  }

  isListTypeSelected(actionParameterType?: ActionParameterType): boolean {
    if(!actionParameterType || !actionParameterType.typeDef) {
      return false;
    }
    return JSON.parse(actionParameterType?.typeDef).type === 'list';
  }

  getListValues(actionParameterType?: ActionParameterType): string[] {
    if(!actionParameterType || !actionParameterType.typeDef) {
      return [];
    }
    return JSON.parse(actionParameterType?.typeDef).values;
  }

  listValueAllowsEmpty(listParamType?: ActionParameterType) {
    if (listParamType && listParamType.typeDef) {
      const config: any = JSON.parse(listParamType.typeDef).config;
      if (config) {
        return config.allowEmptyValues;
      }
    }
    return true;
  }

  resetNewParameterDefault(event: any) {
    this.newParameterDefaultValue = '';
    if (this.newParameterType && this.newParameterType.typeDef && !this.listValueAllowsEmpty(this.newParameterType)) {
      const config: any = JSON.parse(this.newParameterType.typeDef).config;
      if (config) {
        this.newParameterDefaultValue = config.defaultWhenEmpty;
      }
    }
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

  getValue(object: any): string | null {
    if (object) {
      return object.currentValue;
    }
    return null;
  }

  getName(object: any): string {
    return object.actionParameter.name;
  }

  setValue(object: any, value: string | null): void {
    if (object) {
      object.currentValue = value;
    }
  }

  getObjectTypeDef(rowObject: any): any {
    if (rowObject.actionParameter.actionParameterType) {
      return JSON.parse(rowObject.actionParameter.actionParameterType?.typeDef);
    }
    return null;
  }

  setDefault(rowObject: any): void {
    rowObject.currentValue = rowObject.defaultValue;
  }

  resetParam(param?: ActionParameter, event?: any): void {
    if (param) {
      param.currentValue = param.defaultValue;
    }
    this.consumeEvent(event);
  }

  getSelectedRow(): number {
    return this.selectedRow;
  }

  setSelectedRow(event: any): void {
    this.selectedRow = event as number;
  }
}
