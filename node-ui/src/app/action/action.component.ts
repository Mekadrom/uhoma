import { Component, OnInit, Input } from '@angular/core';
import { NodeAction } from '../models/node-action';
import { ActionParameter } from '../models/action-parameter';

@Component({
  selector: 'app-action',
  templateUrl: './action.component.html',
  styleUrls: ['./action.component.css']
})
export class ActionComponent implements OnInit {
  displayedColumns: string[] = ['name', 'currentValue'];

  nodeAction?: NodeAction;

  @Input('setAction')
  public set setAction(nodeAction: NodeAction | undefined) {
    console.log(JSON.stringify(nodeAction));
    this.nodeAction = nodeAction;
    this.refresh();
  }

  constructor() { }

  refresh(): void {
    this.nodeAction?.parameters.forEach(param => param.currentValue = param.defaultValue);
  }

  ngOnInit(): void {
  }

  omitSpecialChar(event: any) {
    let k = event.charCode;
    return((k > 64 && k < 91) || (k > 96 && k < 123) || k == 8 || k == 32 || (k >= 48 && k <= 57));
  }

  isParamTextField(param: ActionParameter): boolean {
    return param.type === 'string';
  }

  runActionWithParams(): void {

  }
}
