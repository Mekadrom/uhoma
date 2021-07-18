import { Component, OnInit, Input, AfterViewInit } from '@angular/core';

import { NodeComponent } from '../node/node.component';

import { Node } from '../models/node';
import { NodeAction } from '../models/node-action';

@Component({
  selector: 'app-node-action',
  templateUrl: './node-action.component.html',
  styleUrls: ['./node-action.component.css']
})
export class NodeActionComponent implements OnInit, AfterViewInit {
  node?: Node;

  @Input('setNode')
  public set setNode(node: Node | undefined) {
    this.node = node;
    this.refresh();
  }

  actionNameSearchTerm: string = '';

  filteredActions?: NodeAction[];

  activeAction?: NodeAction;

  constructor() { }

  setActiveAction(nodeAction: NodeAction): void {
    this.activeAction = nodeAction;
  }

  getActiveAction(): NodeAction | undefined {
    return this.activeAction;
  }

  clearSearchBar(): void {
    this.actionNameSearchTerm = '';
    this.refresh();
  }

  refresh(): void {
    this.activeAction = undefined;
    this.filteredActions = this.node?.publicActions
      .filter(it => this.actionNameSearchTerm === '' || it.name.toUpperCase().indexOf(this.actionNameSearchTerm.toUpperCase()) != -1);
  }

  omitSpecialChar(event: any) {
    let k = event.charCode;
    return((k > 64 && k < 91) || (k > 96 && k < 123) || k == 8 || k == 32 || (k >= 48 && k <= 57));
  }

  getAction(nodeAction: NodeAction): string {
    return JSON.stringify(nodeAction);
  }

  ngOnInit(): void {
  }

  ngAfterViewInit(): void {
    this.refresh();
  }
}
