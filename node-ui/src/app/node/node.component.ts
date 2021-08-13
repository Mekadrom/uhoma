import { Component, OnInit, AfterViewInit, Input } from '@angular/core';

import { ToastrService } from 'ngx-toastr';

import { Node } from '../models/node';
import { Room } from '../models/room';
import { RoomLink } from '../models/room-link';
import { NodeAction } from '../models/node-action';
import { NodeService } from '../services/node.service';
import { RoomService } from '../services/room.service';

import { NodeComparator } from './node-comparator';

@Component({
  selector: 'app-node',
  templateUrl: './node.component.html',
  styleUrls: ['./node.component.css']
})
export class NodeComponent implements OnInit, AfterViewInit {
  nodeNameSearchTerm: string = '';
  roomNameSearchTerm: string = '';

  selectedNode?: Node;

  loading: boolean = true;
  failedToLoad: boolean = false;

  savedNodes: Node[] = [];
  nodes: Node[] = [];
  filteredNodes: Node[] = [];

  rooms: Room[] = [];

  constructor(private nodeService: NodeService,
              private roomService: RoomService,
              private toastr: ToastrService) { }

  setSelectedNode(node: Node) {
    this.selectedNode = node;
  }

  getSelectedNode(): Node | undefined {
    return this.selectedNode;
  }

  filter(): void {
    this.filteredNodes = this.nodes
      .filter(node => this.nodeNameSearchTerm === '' || node.name.toUpperCase().indexOf(this.nodeNameSearchTerm.toUpperCase()) != -1)
      .filter(node => this.roomNameSearchTerm === '' || node.room.name.toUpperCase().indexOf(this.roomNameSearchTerm.toUpperCase()) != -1);
  }

  fetchData(): void {
    this.loading = true;
    this.failedToLoad = false;
    this.nodeService.getNodes().subscribe(
      (data: Node[]) => {
        this.savedNodes = data;
        this.nodes = JSON.parse(JSON.stringify(this.savedNodes)) as Node[];
        this.loading = false;
        this.failedToLoad = false;
        this.postFetch();
      },
      err => {
        this.loading = false;
        this.failedToLoad = true;
        this.savedNodes = [];
        this.nodes = [];
        this.toastr.error(err.message, 'Connection error');
        this.postFetch();
      }
    );
  }

  fetchRooms(): void {
    this.roomService.getRooms({ name: '' }).subscribe(
      (data: Room[]) => {
        this.rooms = data;
        this.postFetch();
      },
      err => {
      }
    );
  }

  postFetch(): void {
    this.selectedNode = undefined;
    this.filter();
  }

  notifyUpdate(nodeToUpdate: Node): void {
    for (let i: number = 0; i < this.nodes.length; i++) {
      if (this.nodes[i].nodeSeq === nodeToUpdate.nodeSeq) {
        this.nodes[i] = nodeToUpdate;
        break;
      }
    }
    this.filter();
  }

  omitSpecialChar(event: any) {
    let k = event.charCode;
    return (k > 64 && k < 91) || (k > 96 && k < 123) || k == 8 || k == 32 || (k >= 48 && k <= 57);
  }

  clearSearchBar(): void {
    this.nodeNameSearchTerm = '';
    this.roomNameSearchTerm = '';
  }

  saveEnabled(): boolean {
    if (!this.savedNodes || !this.nodes) {
      return false;
    }
    return NodeComparator.nodeArraysDifferent(this.savedNodes, this.nodes);
  }

  save(): void {
    this.nodeService.saveNodes(this.filteredNodes)
    .subscribe(
      (resp: Node[]) => {
        this.nodes = resp;
        this.filter();
        this.selectedNode = undefined;
        this.toastr.success('Save complete.');
      },
      (err: any) => {
        this.toastr.error(err.message, 'Failed to save:');
      }
    );
  }

  reset(): void {
    this.nodes = JSON.parse(JSON.stringify(this.savedNodes)) as Node[];
    this.selectedNode = undefined;
    this.filter();
  }

  refresh(): void {
    this.nodes = [];
    this.rooms = [];
    this.fetchRooms();
    this.fetchData();
  }

  clearAndRefresh(): void {
    this.clearSearchBar();
    this.refresh();
  }

  ngAfterViewInit(): void {
    this.clearAndRefresh();
  }

  ngOnInit(): void {
  }
}
