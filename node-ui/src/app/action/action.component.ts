import { Component, OnInit, Input, NgZone } from '@angular/core';

import { ToastrService } from 'ngx-toastr';

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

  savedNodeAction?: NodeAction;
  nodeAction?: NodeAction;

  actionParameterTypes: ActionParameterType[] = [];

  newParameterType?: ActionParameterType;

  newParameterName: string = '';

  newParameterDefaultValue?: string;

  @Input('setAction')
  public set setAction(nodeAction: NodeAction | undefined) {
    this.savedNodeAction = nodeAction;
    this.nodeAction = { ...nodeAction } as NodeAction;
    this.refresh();
  }

  constructor(private webSocket: WebSocketService,
              private nodeService: NodeService,
              private toastr: ToastrService,
              private ngZone: NgZone) { }

  refresh(): void {
    if (this.nodeAction) {
      this.nodeAction.parameters.forEach(param => this.setDefaultValue(param));
      this.nodeAction.parameters.sort((a: ActionParameter, b: ActionParameter) => (!a.actionParameterSeq || !b.actionParameterSeq) ? 0 : (a.actionParameterSeq > b.actionParameterSeq) ? 1 : -1)
      for (let i = 0; i < this.nodeAction?.parameters.length; i++) {
        this.nodeAction.parameters[i].rowNum = i;
      }
      this.nodeAction.parameters = this.nodeAction.parameters.slice();
    }
    this.selectedRow = -1;
  }

  setDefaultValue(param: ActionParameter): void {
    param.currentValue = param.defaultValue;
    if (!param.currentValue) {
      if (this.isListTypeSelected(param.actionParameterType)) {
        const config: any = this.getListConfig(param.actionParameterType);
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
    this.setDefault(param);
    this.nodeAction?.parameters.push(param);
    this.refresh();
  }

  removeSelected(): void {
    if (this.nodeAction && this.nodeAction.parameters && this.selectedRow !== -1) {
      for (let i = 0; i < this.nodeAction.parameters.length; i++) {
        const param = this.nodeAction.parameters[i];
        if (param) {
          if (i == this.selectedRow) {
            this.nodeAction.parameters.splice(i, 1);
          } else if (i > this.selectedRow && param.rowNum) {
            param.rowNum = param.rowNum - 1;
          }
        }
      }
    }
    this.refresh();
  }

  saveEnabled(): boolean {
    return JSON.stringify(this.savedNodeAction) != JSON.stringify(this.nodeAction);
  }

  save(): void {
    this.nodeService.saveNodeAction(this.nodeAction)
    .subscribe((resp: any) => {
      this.savedNodeAction = resp;
      this.nodeAction = { ...this.savedNodeAction } as NodeAction;
      this.refresh();
    },
    (err) => {
      this.toastr.error(err.message, 'Failed to save:');
    });
  }

  reset(): void {
    if (this.nodeAction) {
      this.nodeAction.parameters.forEach(param => this.resetParam(param, null));
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

  setValue(object: any, value: string | null): void {
    if (object) {
      object.currentValue = value;
    }
  }

  getObjectTypeDef(rowObject: any): any {
    if (rowObject.actionParameterType) {
      return JSON.parse(rowObject.actionParameterType?.typeDef);
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
