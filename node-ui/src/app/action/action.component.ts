import { Component, OnInit, Input } from '@angular/core';
import { NodeAction } from '../models/node-action';

@Component({
  selector: 'app-action',
  templateUrl: './action.component.html',
  styleUrls: ['./action.component.css']
})
export class ActionComponent implements OnInit {
  nodeAction?: NodeAction;

  @Input('setAction')
  public set setAction(nodeAction: NodeAction | undefined) {
    this.nodeAction = nodeAction;
    this.refresh();
  }

  constructor() { }

  refresh(): void {
  }

  ngOnInit(): void {
  }
}
