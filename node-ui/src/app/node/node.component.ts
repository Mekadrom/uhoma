import { Component, OnInit, AfterViewInit, Input } from '@angular/core';
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

  activeNode?: Node;

  nodes: Node[] = [
  ];

  filteredNodes: Node[] = [
  ];

  rooms: Room[] = [
  ];

  constructor(private nodeService: NodeService, private roomService: RoomService, private toastr: ToastrService) { }

  setActiveNode(node: Node) {
    this.activeNode = node;
  }

  getActiveNode(): Node | undefined {
    return this.activeNode;
  }

  filter(): void {
    this.filteredNodes = this.nodes
      .filter(node => this.nodeNameSearchTerm === '' || node.name.toUpperCase().indexOf(this.nodeNameSearchTerm.toUpperCase()) != -1)
      .filter(node => this.roomNameSearchTerm === '' || node.room.name.toUpperCase().indexOf(this.roomNameSearchTerm.toUpperCase()) != -1);
  }

  fetchData(): void {
    this.nodeService.getNodes().subscribe(
      (data: Node[]) => {
        this.nodes = data;
        this.postFetch();
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
        this.postFetch();
      },
      err => {
        this.toastr.error(err.message, 'Connection error');
      }
    );
  }

  postFetch(): void {
    this.activeNode = undefined;
    this.filter();
  }

  omitSpecialChar(event: any) {
    let k = event.charCode;
    return((k > 64 && k < 91) || (k > 96 && k < 123) || k == 8 || k == 32 || (k >= 48 && k <= 57));
  }

  clearSearchBar(): void {
    this.nodeNameSearchTerm = '';
    this.roomNameSearchTerm = '';
    this.refresh();
  }

  refresh(): void {
    this.fetchRooms();
    this.fetchData();
  }

  ngAfterViewInit(): void {
    this.clearSearchBar();
    this.refresh();
  }

  ngOnInit(): void {
    this.fetchRooms();
  }
}
