import { Component, OnInit } from '@angular/core';
import { Node } from '../models/node';
import { Room } from '../models/room';
import { RoomLink } from '../models/room-link';
import { NodeAction } from '../models/node-action';

@Component({
  selector: 'app-node',
  templateUrl: './node.component.html',
  styleUrls: ['./node.component.css']
})
export class NodeComponent implements OnInit {

  room: Room = {
    id: 1,
    name: 'test room',
    room_links: []
  };

  action1: NodeAction = {
    id: 1,
    name: 'test action 1'
  };

  action2: NodeAction = {
    id: 2,
    name: 'test action 2'
  };

  node1: Node = {
    id: 1,
    name: 'test node 1',
    room: this.room,
    node_actions: [ this.action1, this.action2 ]
  };

  node2: Node = {
    id: 2,
    name: 'test node 2',
    room: this.room,
    node_actions: [ this.action2, this.action1 ]
  };

  nodes: Node[] = [
    this.node1, this.node2, this.node2, this.node1, this.node1, this.node1, this.node1, this.node1, this.node1, this.node1, this.node1, this.node1, this.node1, this.node1, this.node1, this.node1, this.node1
  ]

  selectedNodeAction?: NodeAction;
  onSelect(nodeAction: NodeAction): void {
    this.selectedNodeAction = nodeAction;
  }

  constructor(private nodeService: NodeService) { }

  fetchData(): void {
    this.nodeService.get.node()
  }

  ngOnInit(): void {
    this.fetchData();
  }

}
