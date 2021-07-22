import { Component, OnInit, Input, AfterViewInit } from '@angular/core';

import { ToastrService } from 'ngx-toastr';

import { Node } from '../models/node';
import { NodeAction } from '../models/node-action';
import { ActionParameter } from '../models/action-parameter';
import { ActionParameterType } from '../models/action-parameter-type';

import { NodeService } from '../services/node.service';
import { WebSocketService } from '../services/web-socket.service';

import { NodeComparator } from './node-comparator';

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

  constructor(private nodeService: NodeService,
              private toastr: ToastrService) { }

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

  saveEnabled(): boolean {
    if (!this.savedNode || !this.node) {
      return false;
    }
    return NodeComparator.nodesDifferent(this.savedNode, this.node);
  }

  save(): void {
    this.nodeService.saveNode(this.node)
    .subscribe((resp: any) => {
      this.savedNode = resp;
      this.reset();
      this.refresh();
      this.toastr.success('Save complete.');
    },
    (err: any) => {
      this.toastr.error(err.message, 'Failed to save:');
    });
  }

  reset(): void {
    this.node = JSON.parse(JSON.stringify(this.savedNode)) as Node;
  }
}
