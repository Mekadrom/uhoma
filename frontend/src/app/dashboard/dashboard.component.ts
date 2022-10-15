import { AfterViewInit, Component, EventEmitter, Inject  } from '@angular/core';
import { FormControl, Validators } from '@angular/forms';
import { CookieService } from 'ngx-cookie-service';
import { ToastrService } from 'ngx-toastr';

import { Action, ActionHandler, ActionParameter, ActionParameterType, DashboardBreadCrumb, Home, Node, Room } from '../models';
import { ActionHandlerService, ActionParameterTypeService, AuthService, CommonUtilsService, HomeService, NodeService, RoomService, UserProviderService, WebSocketService } from '../services';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements AfterViewInit {
  nonEmptyFormControl = new FormControl('', [
    Validators.required
  ]);

  nodeDisplayedColumns: string[] = [ 'name', 'location' ];
  actionDisplayedColumns: string[] = [ 'name', 'actionHandler' ];
  parameterDisplayedColumns: string[] = [ 'name', 'value' ];

  homeSearchCriteria?: Home;
  roomSearchCriteria?: Room;
  nodeNameSearchCriteria: string = '';

  editingNodeRow: number = -1;
  editingActionRow: number = -1;

  actionNameSearchCriteria = '';

  savedNodes: Node[] = [];
  mutableNodes: Node[] = [];

  homes: Home[] = [];
  rooms: Room[] = [];

  actionHandlers: ActionHandler[] = [];

  selectedNodeRow: number = -1;
  selectedActionRow: number = -1;
  selectedParameterRow: number = -1;

  loading: boolean = true;
  failedToLoad: boolean = false;
  subscribed: boolean = false;

  newActionName: string = '';
  newActionActionHandler?: ActionHandler;

  actionParameterTypes: ActionParameterType[] = [];

  newParameterType?: ActionParameterType;
  newParameterName: string = '';
  newParameterDefaultValue: any = '';

  editingNodeName: string = '';
  editingNodeRoomSeq: number = -1;

  editingActionName: string = '';
  editingActionHandlerSeq: number = -1;

  editingParameterRow: number = -1;

  breadcrumbEmitter: EventEmitter<DashboardBreadCrumb>;

  constructor(private actionHandlerService: ActionHandlerService,
              private actionParameterTypeService: ActionParameterTypeService,
              private authService: AuthService,
              public commonUtilsService: CommonUtilsService,
              private cookieService: CookieService,
              private homeService: HomeService,
              private nodeService: NodeService,
              private roomService: RoomService,
              private toastr: ToastrService,
              private userProviderService: UserProviderService,
              private webSocketService: WebSocketService) {
    this.breadcrumbEmitter = new EventEmitter<DashboardBreadCrumb>();
  }

  ngAfterViewInit(): void {
    const jwt: string | null | undefined = this.cookieService.get('bearer');
    if (!this.userProviderService.getUserView()) {
      this.authService.refreshUserView();
    }
    if (this.cookieService.get('bearer') && this.userProviderService.getUserView() && !this.webSocketService.isConnected()) {
      this.webSocketService.attach(this.cookieService.get('bearer'));
      if (!this.subscribed) {
        this.webSocketService.userResponse.subscribe((msg) => {
          this.toastr.info(msg);
        });
        this.subscribed = true;
      }
    }
    this.fetch(false);
  }

  fetch(skipHomeFetch: boolean): void {
    this.loading = true;
    this.failedToLoad = false;
    if (skipHomeFetch) {
      this.fetchRooms();
      this.fetchActionHandlers();
      this.fetchParameterTypes();
    } else {
      this.fetchHomes();
    }
  }

  fetchHomes(selectHome?: string): void {
    this.homeService.getHomes().subscribe(
      (resp: Home[]) => {
        this.homes = resp;
        if (this.homes && this.homes.length > 0) {
          if (!this.homeSearchCriteria) {
            this.homeSearchCriteria = this.homes.findIndex((home) => home.name === selectHome) > -1 ? this.homes.find((home) => home.name === selectHome) : this.homes[0];
            this.breadcrumbEmitter.emit(this.getBreadcrumbData());
          }
          this.fetchRooms();
          this.fetchActionHandlers();
          this.fetchParameterTypes();
        }
      },
      (err: any) => {
        this.loading = false
        this.failedToLoad = true;
        this.toastr.error(err.message, 'Failure fetching homes');
      }
    )
  }

  fetchRooms(): void {
    const searchCriteria: Home = this.homeSearchCriteria ? this.homeSearchCriteria : this.homes[0];
    this.roomService.getRooms(searchCriteria).subscribe(
      (resp: Room[]) => {
        this.rooms = resp;
        this.fetchNodes();
      },
      (err: any) => {
        this.loading = false
        this.failedToLoad = true;
        this.toastr.error(err.message, 'Failure fetching rooms');
      }
    );
  }

  fetchNodes(): void {
    this.nodeService.getNodes(this.homeSearchCriteria).subscribe(
      (resp: Node[]) => {
        this.loading = false;
        this.failedToLoad = false;
        this.setNodeData(resp);
      },
      (err: any) => {
        this.loading = false
        this.failedToLoad = true;
        this.toastr.error(err.message, 'Failure fetching nodes');
      }
    );
  }

  fetchActionHandlers(): void {
    this.actionHandlerService.getActionHandlers().subscribe(
      (resp: ActionHandler[]) => {
        this.actionHandlers = resp;
      },
      (err: any) => {
        this.loading = false
        this.failedToLoad = true;
        this.toastr.error(err.message, 'Failure fetching action handlers');
      }
    );
  }

  fetchParameterTypes(): void {
    this.actionParameterTypeService.getActionParameterTypes().subscribe(
      (resp: ActionParameterType[]) => {
        this.actionParameterTypes = resp;
      },
      (err: any) => {
        this.loading = false
        this.failedToLoad = true;
        this.toastr.error(err.message, 'Failure fetching parameter types');
      }
    );
  }

  getHomeIndex(homeName: string): number {
    return this.homes.findIndex(home => home.name === homeName);
  }

  setNodeData(nodes: Node[]): void {
    this.savedNodes = nodes;
    this.mutableNodes = JSON.parse(JSON.stringify(this.savedNodes)) as Node[];
  }

  // region nodes
  getNode(index: number): Node | null {
    if (this.mutableNodes.length > 0
          && index >= 0
          && index < this.mutableNodes.length) {
      return this.mutableNodes[index];
    }
    return null;
  }

  getSelectedNodeRow(): number {
    return this.selectedNodeRow;
  }

  selectNode(node: Node) {
    const rowOf: number = this.getNodeRow(node);
    if (rowOf !== this.editingNodeRow) {
      if (this.selectedNodeRow === rowOf) {
        this.selectedNodeRow = -1;
      } else {
        this.selectedNodeRow = rowOf;
      }
      this.selectedActionRow = -1;
      this.selectedParameterRow = -1;
    }
    this.breadcrumbEmitter.emit(this.getBreadcrumbData());
  }

  getNodeRow(findNode: Node): number {
    return this.mutableNodes.findIndex(node => node === findNode);
  }

  showNode(node: Node): boolean {
    const roomName: string | undefined = this.roomSearchCriteria?.name;
    let roomNameSearch: string = '';
    if (roomName) {
      roomNameSearch = roomName.toUpperCase();
    }
    return (this.nodeNameSearchCriteria === '' || node.name.toUpperCase().indexOf(this.nodeNameSearchCriteria.toUpperCase()) !== -1)
            && (this.roomSearchCriteria?.name === '' || node.room.name.toUpperCase().indexOf(roomNameSearch) !== -1);
  }

  getNodeName(node: Node): string {
    return node.name;
  }

  // region actions
  getAction(index: number): Action | null {
    const selectedNode: Node | null = this.getNode(this.selectedNodeRow);
    if (selectedNode
          && selectedNode.actions
          && selectedNode.actions.length > 0
          && index >= 0
          && index < selectedNode.actions.length) {
      return selectedNode.actions[index];
    }
    return null;
  }

  getSelectedActionRow(): number {
    return this.selectedActionRow;
  }

  selectAction(action: Action) {
    const rowOf: number = this.getActionRow(action);
    if (rowOf !== this.editingActionRow) {
      if (this.selectedActionRow === rowOf) {
        this.selectedActionRow = -1;
      } else {
        this.selectedActionRow = rowOf;
      }
      this.selectedParameterRow = -1;
    }
    this.breadcrumbEmitter.emit(this.getBreadcrumbData());
  }

  getActionRow(findAction: Action): number {
    const selectedNode: Node | null = this.getNode(this.selectedNodeRow);
    if (selectedNode) {
      return selectedNode.actions.findIndex(action => action === findAction);
    }
    return -1;
  }

  showAction(action: Action): boolean {
    return this.actionNameSearchCriteria === '' || action.name.toUpperCase().indexOf(this.actionNameSearchCriteria.toUpperCase()) !== -1;
  }

  // region parameters
  getParameter(index: number): ActionParameter | null {
    const selectedAction: Action | null = this.getAction(this.selectedActionRow);
    if (selectedAction
          && selectedAction.parameters
          && selectedAction.parameters.length > 0
          && index >= 0
          && index < selectedAction.parameters.length) {
      return selectedAction.parameters[index];
    }
    return null;
  }

  getSelectedParameterRow(): number {
    return this.selectedParameterRow;
  }

  getParameterRow(findParameter: ActionParameter): number {
    const selectedAction: Action | null = this.getAction(this.selectedActionRow);
    if (selectedAction) {
      return selectedAction.parameters.findIndex(parameter => parameter === findParameter);
    }
    return -1;
  }

  selectParameter(parameter: ActionParameter) {
    const rowOf: number = this.getParameterRow(parameter);
    if (rowOf !== this.editingParameterRow) {
      if (this.selectedParameterRow === rowOf) {
        this.selectedParameterRow = -1;
      } else {
        this.selectedParameterRow = rowOf;
      }
    }
    this.breadcrumbEmitter.emit(this.getBreadcrumbData());
  }

  getFilteredNodeCount(): number {
    let count: number = 0;
    if (this.mutableNodes) {
      for (let i: number = 0; i < this.mutableNodes.length; i++) {
        count += this.showNode(this.mutableNodes[i]) ? 1 : 0;
      }
    }
    return count;
  }

  clearNodeSearchCriteriaAndRefresh(): void {
    this.nodeNameSearchCriteria = '';
    this.roomSearchCriteria = undefined;
    this.fetch(true);
  }

  clearActionSearchCriteria(): void {
    this.actionNameSearchCriteria = '';
  }

  getActionsToShow(): Action[] {
    const selectedNodeRow: number = this.getSelectedNodeRow();
    if (selectedNodeRow !== -1) {
      const selectedNode: Node | null = this.getNode(selectedNodeRow);
      if (selectedNode) {
        return selectedNode.actions;
      }
    }
    return [];
  }

  getParametersToShow(): ActionParameter[] {
    const selectedActionRow: number = this.getSelectedActionRow();
    if (selectedActionRow !== -1) {
      const selectedAction: Action | null = this.getAction(selectedActionRow);
      if (selectedAction) {
        return selectedAction.parameters;
      }
    }
    return [];
  }

  addAction(): void {
    if (!this.newActionName || this.newActionName === '') {
      this.toastr.error('Please enter a name for the new action.');
      return;
    }

    const selectedNodeRow: number = this.getSelectedNodeRow();
    if (selectedNodeRow !== -1) {
      const selectedNode: Node | null = this.getNode(selectedNodeRow);
      if (selectedNode) {
        if (selectedNode.actions.map(action => action.name).indexOf(this.newActionName) !== -1) {
          this.toastr.error('Please enter a unique name for the new action.');
          return;
        }
        selectedNode.actions.push({ name: this.newActionName, ownerNodeSeq: selectedNode.nodeSeq, actionHandler: this.newActionActionHandler, parameters: [] })
        selectedNode.actions = selectedNode.actions.slice(); // updates reference, forces angular view to refresh
      }
    }

    if (!this.newActionActionHandler) {
      this.toastr.warning('Action created with no handler.');
    }

    this.newActionName = '';
    this.newActionActionHandler = undefined;
  }

  deleteAction(): void {
    const selectedNodeRow: number = this.getSelectedNodeRow();
    if (selectedNodeRow !== -1) {
      const selectedNode: Node | null = this.getNode(selectedNodeRow);
      if (selectedNode) {
        const selectedActionRow: number = this.getSelectedActionRow();
        if (selectedActionRow >= 0 && selectedActionRow < selectedNode.actions.length) {
          selectedNode.actions.splice(selectedActionRow, 1);
          selectedNode.actions = selectedNode.actions.slice(); // updates reference, forces angular view to refresh
        }
      }
    }
  }

  addParameter(): void {
    if (!this.newParameterName || this.newParameterName === '') {
      this.toastr.error('Please enter a name for the new parameter.');
      return;
    }

    const selectedActionRow: number = this.getSelectedActionRow();
    if (selectedActionRow !== -1) {
      const selectedAction: Action | null = this.getAction(selectedActionRow);
      if (selectedAction) {
        if (selectedAction.parameters.map(param => param.name).indexOf(this.newParameterName) !== -1) {
          this.toastr.error('Please enter a unique name for the new parameter.');
          return;
        }
        selectedAction.parameters.push({ name: this.newParameterName, actionParameterType: this.newParameterType, defaultValue: this.newParameterDefaultValue, currentValue: this.newParameterDefaultValue })
        selectedAction.parameters = selectedAction.parameters.slice(); // updates reference, forces angular view to refresh
      }
    }

    this.newParameterName = '';
    this.newParameterType = undefined;
    this.newParameterDefaultValue = '';
  }

  deleteParameter(): void {
    const selectedActionRow: number = this.getSelectedActionRow();
    if (selectedActionRow !== -1) {
      const selectedAction: Action | null = this.getAction(selectedActionRow);
      if (selectedAction) {
        const selectedParameterRow: number = this.getSelectedParameterRow();
        console.log(selectedParameterRow);
        if (selectedParameterRow >= 0 && selectedParameterRow < selectedAction.parameters.length) {
          selectedAction.parameters.splice(selectedParameterRow, 1);
          selectedAction.parameters = selectedAction.parameters.slice();
        }
      }
    }
  }

  importAction(): void {
    // todo
  }

  exportAction(): void {
    // todo
  }

  cancelEditingNode(node: Node): void {
    const editingNodeRow: number = this.getNodeRow(node);
    if (this.editingNodeRow !== editingNodeRow) {
      this.editingNodeRow = -1;
    }
    this.commonUtilsService.fixMaterialBug();
  }

  toggleEditingNode(node: Node): void {
    const editingNodeRow: number = this.getNodeRow(node);
    if (this.editingNodeRow === editingNodeRow) {
      this.editingNodeRow = -1;
      this.clearEditingNode();
    } else {
      this.editingNodeName = node.name;
      this.editingNodeRoomSeq = node.room.roomSeq || -1;
      this.editingNodeRow = editingNodeRow;
    }
    this.commonUtilsService.fixMaterialBug();
  }

  clearEditingNode(): void {
    this.editingNodeName = '';
    this.editingNodeRoomSeq = -1;
  }

  saveEditingNode(node: Node): void {
    node.name = !this.editingNodeName || this.editingNodeName === '' ? node.name : this.editingNodeName;
    const room: Room | undefined = this.findRoomFromRoomSeq(this.editingNodeRoomSeq);
    if (room) {
      node.room = room;
    }
    this.editingNodeRow = -1;
    this.commonUtilsService.fixMaterialBug();
  }

  findRoomFromRoomSeq(roomSeq: number): Room | undefined {
    for (let i: number = 0; i < this.rooms.length; i++) {
      if (this.rooms[i].roomSeq === roomSeq) {
        return this.rooms[i];
      }
    }
    return undefined;
  }

  cancelEditingAction(action: Action): void {
    const editingActionRow: number = this.getActionRow(action);
    if (this.editingActionRow !== editingActionRow) {
      this.editingActionRow = -1;
    }
    this.commonUtilsService.fixMaterialBug();
  }

  toggleEditingAction(action: Action): void {
    const editingActionRow: number = this.getActionRow(action);
    if (this.editingActionRow === editingActionRow) {
      this.editingActionRow = -1;
      this.clearEditingAction();
    } else {
      this.editingActionRow = editingActionRow;
      this.editingActionName = action.name;
      this.editingActionHandlerSeq = action.actionHandler?.actionHandlerSeq || -1;
    }
    this.commonUtilsService.fixMaterialBug();
  }

  clearEditingAction(): void {
    this.editingActionName = '';
    this.editingActionHandlerSeq = -1;
  }

  saveEditingAction(action: Action): void {
    action.name = !this.editingActionName || this.editingActionName === '' ? action.name : this.editingActionName;
    action.actionHandler = this.findActionHandlerByActionHandlerSeq(this.editingActionHandlerSeq);
    this.editingActionRow = -1;
    this.commonUtilsService.fixMaterialBug();
  }

  findActionHandlerByActionHandlerSeq(actionHandlerSeq: number): ActionHandler | undefined {
    for (let i: number = 0; i < this.actionHandlers.length; i++) {
      if (this.actionHandlers[i].actionHandlerSeq === actionHandlerSeq) {
        return this.actionHandlers[i];
      }
    }
    return undefined;
  }

  getParameterDatasource(): ActionParameter[] {
    const parameters: ActionParameter[] | undefined = this.getAction(this.getSelectedActionRow())?.parameters;
    if (parameters) {
      return parameters;
    }
    return [];
  }

  getParameterTypeDef(rowObject: any): any {
    if (rowObject.actionParameterType) {
      return rowObject.actionParameterType?.typeDef;
    }
    return null;
  }

  resetNewParameterDefault(event: any) {
    this.newParameterDefaultValue = '';
    if (this.newParameterType && this.newParameterType.typeDef && !this.commonUtilsService.listValueAllowsEmpty(this.newParameterType.typeDef)) {
      const config: any = JSON.parse(this.newParameterType.typeDef).config;
      if (config) {
        this.newParameterDefaultValue = config.defaultWhenEmpty;
      }
    }
  }

  save(): void {
    console.log(JSON.stringify(this.mutableNodes));
    this.nodeService.saveNodes(this.mutableNodes).subscribe(
      (resp: Node[]) => {
        this.setNodeData(resp);
        this.toastr.success('Save successful.');
      },
      (err: any) => {
        this.toastr.error(err.message, 'Save failed.');
      }
    );
  }

  resetAll(): void {
    this.setNodeData(this.savedNodes);
    this.setAllDefaults();
  }

  setParameterDefault(param: ActionParameter): void {
    param.currentValue = param.defaultValue;
  }

  setParameterDefaults(action: Action): void {
    action.parameters.forEach((param: ActionParameter) => this.setParameterDefault(param));
  }

  setDefaults(node: Node): void {
    node.actions.forEach((action: Action) => this.setParameterDefaults(action));
  }

  setAllDefaults(): void {
    this.mutableNodes.forEach((node: Node) => this.setDefaults(node));
  }

  setSelectedParameterRow(event: any) {
    this.selectedParameterRow = event as number;
    this.breadcrumbEmitter.emit(this.getBreadcrumbData());
  }

  setEditingParameterRow(event: any) {
    this.editingParameterRow = event as number;
    this.setSelectedParameterRow(event);
  }

  executeAction(): void {
    const action: Action | null = this.getAction(this.getSelectedActionRow());
    if (action) {
      this.webSocketService.executeAction(action, false, '');
      if (!this.subscribed) {
        this.webSocketService.userResponse.subscribe((msg) => {
          this.toastr.info(JSON.parse(msg).body);
        });
        this.subscribed = true;
      }
    }
  }

  changeHomeSelection(event: string): void {
    console.debug('changeHomeSelectionEmit received: ' + event);
    this.homeSearchCriteria = this.homes[this.getHomeIndex(event)];
    this.fetchHomes();
  }

  getBreadcrumbData(): DashboardBreadCrumb {
    return {
      home: this.homeSearchCriteria?.name,
      homes: this.homes.map((home: Home) => home.name),
      room: this.getNode(this.getSelectedNodeRow())?.room?.name,
      node: this.getNode(this.getSelectedNodeRow())?.name,
      action: this.getAction(this.getSelectedActionRow())?.name,
      parameter: this.getParameter(this.getSelectedParameterRow())?.name
    } as DashboardBreadCrumb;
  }
}
