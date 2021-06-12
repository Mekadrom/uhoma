import { Component, OnInit } from '@angular/core';
import { Node } from '../models/node';
import { Room } from '../models/room';
import { RoomLink } from '../models/room-link';
import { NodeAction } from '../models/node-action';
import { NodeService } from '../services/node.service';

@Component({
  selector: 'app-node',
  templateUrl: './node.component.html',
  styleUrls: ['./node.component.css']
})
export class NodeComponent implements OnInit {
//   room: Room = {
//     roomSeq: 1,
//     name: 'test room'
//   };
//
//   action1: NodeAction = {
//     actionSeq: 1,
//     name: 'test action 1',
//     handler: 'testhandler'
//   };
//
//   action2: NodeAction = {
//     actionSeq: 2,
//     name: 'test action 2',
//     handler: 'testhandler'
//   };
//
//   node1: Node = {
//     nodeSeq: 1,
//     name: 'test node 1',
//     room: this.room,
//     publicActions: [ this.action1, this.action2 ]
//   };
//
//   node2: Node = {
//     nodeSeq: 2,
//     name: 'test node 2',
//     room: this.room,
//     publicActions: [ this.action2, this.action1 ]
//   };

  nodes: Node[] = [
//     this.node1, this.node2, this.node2, this.node1, this.node1, this.node1, this.node1, this.node1, this.node1, this.node1, this.node1, this.node1, this.node1, this.node1, this.node1, this.node1, this.node1
  ]

  selectedNodeAction?: NodeAction;
  onSelect(nodeAction: NodeAction): void {
    this.selectedNodeAction = nodeAction;
  }

  constructor(private nodeService: NodeService) { }

  fetchData(): void {
    this.nodeService.getNodes(null).subscribe((data: Node[]) => {
      this.nodes = data;
    })
  }

  ngOnInit(): void {
    this.fetchData();
  }
}
