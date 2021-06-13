import { Component, OnInit, AfterViewInit } from '@angular/core';
import { ToastrService } from 'ngx-toastr';

import { Node } from '../models/node';
import { Room } from '../models/room';
import { RoomLink } from '../models/room-link';
import { NodeAction } from '../models/node-action';
import { NodeService } from '../services/node.service';
import { RoomService } from '../services/room.service';

@Component({
  selector: 'app-node',
  templateUrl: './node.component.html',
  styleUrls: ['./node.component.css']
})
export class NodeComponent implements OnInit, AfterViewInit {
  nodeNameSearchTerm: string = '';
  roomNameSearchTerm: string = '';

  nodes: Node[] = [
  ];

  rooms: Room[] = [
  ];

  selectedNodeAction?: NodeAction;
  onSelect(nodeAction: NodeAction): void {
    this.selectedNodeAction = nodeAction;
  }

  constructor(private nodeService: NodeService, private roomService: RoomService, private toastr: ToastrService) { }

  fetchData(): void {
    this.nodeService.getNodes(this.nodeNameSearchTerm, this.roomNameSearchTerm).subscribe(
      (data: Node[]) => {
        this.nodes = data;
      },
      err => {
        console.log(JSON.stringify(err))
        this.toastr.error(err.message, 'Connection error');
      }
    );
  }

  fetchRooms(): void {
    this.roomService.getRooms('').subscribe(
      (data: Room[]) => {
        this.rooms = data;
      },
      err => {
        this.toastr.error(err.message, 'Connection error');
      }
    );
  }

  clearSearchBar(): void {
    this.nodeNameSearchTerm = '';
    this.roomNameSearchTerm = '';
  }

  refresh(): void {
    this.fetchRooms();
    this.fetchData();
  }

  onSearchTermChange(): void {
    this.refresh();
  }

  roomSelection(roomName: string) {
    this.roomNameSearchTerm = roomName;
    this.refresh();
  }

  ngAfterViewInit(): void {
    this.clearSearchBar();
    this.refresh();
  }

  ngOnInit(): void {
    this.fetchRooms();
  }
}
