import { Component, AfterViewInit } from '@angular/core';
import { FormControl, Validators } from '@angular/forms';
import { CookieService } from 'ngx-cookie-service';
import { ToastrService } from 'ngx-toastr';

import { Node, Room, Action, ActionHandler, ActionParameter, ActionParameterType } from '../models';
import { ActionHandlerService, ActionParameterTypeService, AuthService, CommonUtilsService, NodeService, RoomService, UserProviderService, WebSocketService } from '../services';

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

  nodeNameSearchCriteria = '';
  roomNameSearchCriteria = '';

  editingNodeRow: number = -1;
  editingActionRow: number = -1;

  actionNameSearchCriteria = '';

  savedNodes: Node[] = [];
  mutableNodes: Node[] = [];

  rooms: Room[] = [];

  actionHandlers: ActionHandler[] = [];

  selectedNodeRow: number = -1;
  selectedActionRow: number = -1;
  selectedParameterRow: number = -1;

  loading: boolean = true;
  failedToLoad: boolean = false;

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

  constructor(private actionHandlerService: ActionHandlerService,
              private actionParameterTypeService: ActionParameterTypeService,
              private authService: AuthService,
              public commonUtilsService: CommonUtilsService,
              private cookieService: CookieService,
              private nodeService: NodeService,
              private roomService: RoomService,
              private toastr: ToastrService,
              private userProviderService: UserProviderService,
              private webSocketService: WebSocketService) { }

  ngAfterViewInit(): void {
    if (this.authService.isJwtExpired(this.cookieService.get('bearer'))) {
      this.reauth();
    }
    if (!this.userProviderService.getUserView()) {
      this.authService.refreshUserView();
    }
    if (this.cookieService.get('bearer') && this.userProviderService.getUserView() && !this.webSocketService.isConnected()) {
      this.webSocketService.attach(this.cookieService.get('bearer'));
    }
    this.fetch();
  }

  reauth(): void {
    const refreshToken: string = this.cookieService.get('refresh');
    if(refreshToken && !this.authService.isJwtExpired(refreshToken)) {
      this.authService.refreshJwt(refreshToken);
    }
  }

  fetch(): void {
    this.loading = true;
    this.failedToLoad = false;
    this.fetchNodes();
    this.fetchRooms();
    this.fetchActionHandlers();
    this.fetchParameterTypes();
  }

  fetchNodes(): void {
    this.nodeService.getNodes().subscribe(
      (resp: Node[]) => {
        this.loading = false;
        this.failedToLoad = false;
        this.setNodeData(resp);
        console.log(JSON.stringify(this.savedNodes));
      },
      (err: any) => {
        this.loading = false
        this.failedToLoad = true;
        this.toastr.error(err.message, 'Failure to fetch data');
      }
    );
  }

  fetchRooms(): void {
    this.roomService.getRooms({ name: '' }).subscribe(
      (resp: Room[]) => {
        this.rooms = resp;
      },
      (err: any) => {
        // swallow, any error here is likely already printed with the failure to fetch node data
      }
    );
  }

  fetchActionHandlers(): void {
    this.actionHandlerService.getActionHandlers().subscribe(
      (resp: ActionHandler[]) => {
        this.actionHandlers = resp;
      },
      (err: any) => {
        // ||
      }
    )
  }

  fetchParameterTypes(): void {
    this.actionParameterTypeService.getActionParameterTypes().subscribe(
      (resp: ActionParameterType[]) => {
        this.actionParameterTypes = resp;
      },
      (err: any) => {
        // ||
      }
    );
  }

  setNodeData(nodes: Node[]): void {
    this.savedNodes = nodes;
    this.mutableNodes = JSON.parse(JSON.stringify(this.savedNodes)) as Node[];
  }

  refresh(): void {
    this.fetch();
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
  }

  getNodeRow(findNode: Node): number {
    return this.mutableNodes.findIndex(node => node === findNode);
  }

  showNode(node: Node): boolean {
    return (this.nodeNameSearchCriteria === '' || node.name.toUpperCase().indexOf(this.nodeNameSearchCriteria.toUpperCase()) !== -1)
            && (this.roomNameSearchCriteria === '' || node.room.name.toUpperCase().indexOf(this.roomNameSearchCriteria.toUpperCase()) !== -1);
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
    this.roomNameSearchCriteria = '';
    this.refresh();
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
  }

  setEditingParameterRow(event: any) {
    this.editingParameterRow = event as number;
  }

  executeAction(): void {
    // todo: websocket integration
    const action: Action | null = this.getAction(this.getSelectedActionRow());
    if (action) {
      this.webSocketService.executeAction(action);
    }
  }
}