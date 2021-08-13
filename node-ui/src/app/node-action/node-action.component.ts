import { Component, OnInit, Input, AfterViewInit } from '@angular/core';

import { ToastrService } from 'ngx-toastr';

import { Node } from '../models/node';
import { NodeAction } from '../models/node-action';
import { ActionParameter } from '../models/action-parameter';
import { ActionParameterType } from '../models/action-parameter-type';

import { NodeService } from '../services/node.service';
import { WebSocketService } from '../services/web-socket.service';

@Component({
  selector: 'app-node-action',
  templateUrl: './node-action.component.html',
  styleUrls: ['./node-action.component.css']
})
export class NodeActionComponent implements OnInit, AfterViewInit {
  savedNode?: Node;
  node?: Node;

  @Input('setNode')
  public set setNode(node: Node | undefined) {
    this.savedNode = node;
    this.savedNode?.publicActions.forEach(action => action.parameters.sort((a: ActionParameter, b: ActionParameter) => (!a.actionParameterSeq || !b.actionParameterSeq) ? 0 : (a.actionParameterSeq > b.actionParameterSeq) ? 1 : -1));
    this.node = JSON.parse(JSON.stringify(this.savedNode)) as Node;
    this.refresh();
  }

  actionNameSearchTerm: string = '';

  filteredActions?: NodeAction[];

  activeAction?: NodeAction;

  newActionName: string = '';

  newActionHandler: string = '';

  constructor(private nodeService: NodeService,
              private toastr: ToastrService) { }

  toggleActiveAction(nodeAction: NodeAction): void {
    if (this.activeAction === nodeAction) {
      this.activeAction = undefined;
    } else {
      this.activeAction = nodeAction;
    }
  }

  getActiveAction(): NodeAction | undefined {
    return this.activeAction;
  }

  clearSearchBar(): void {
    this.actionNameSearchTerm = '';
    this.refresh();
  }

  refresh(): void {
    this.filteredActions = this.node?.publicActions
      .filter(it => this.actionNameSearchTerm === '' || it.name.toUpperCase().indexOf(this.actionNameSearchTerm.toUpperCase()) != -1);
  }

  omitSpecialChar(event: any) {
    let k = event.charCode;
    return((k > 64 && k < 91) || (k > 96 && k < 123) || k == 8 || k == 32 || (k >= 48 && k <= 57));
  }

  ngOnInit(): void {
  }

  ngAfterViewInit(): void {
    this.refresh();
  }

  add(): void {
    if (!this.savedNode || !this.node) {
      return;
    }
    if (this.newActionName === '') {
      this.toastr.error('Please type a name for the new action.');
      return;
    }
    if (this.node.publicActions.some(action => action.name === this.newActionName)) {
      this.toastr.error('An action with that name already exists. Please choose a different name.');
      return;
    }

    const newAction: NodeAction = { name: this.newActionName, ownerNode: this.savedNode, handler: this.newActionHandler, parameters: [] };
    this.node?.publicActions?.push(newAction);
    this.toggleActiveAction(newAction);
    this.refresh();

    if (this.newActionHandler === '') {
      this.toastr.warning('Action created with empty handler definition.');
    }
  }

  isActionActive(nodeAction: NodeAction): boolean {
    return JSON.stringify(nodeAction) === JSON.stringify(this.activeAction);
  }

  removeActive(): void {
    if (this.node && this.activeAction) {
      const index: number = this.node.publicActions.indexOf(this.activeAction, 0);
      if (index > -1) {
        console.log('index: ' + index);
        this.node?.publicActions.splice(index, 1);
        this.refresh();
        this.activeAction = this.node.publicActions[0];
      }
    }
  }
}
