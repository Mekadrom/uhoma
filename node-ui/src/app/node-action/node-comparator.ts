import { Node } from '../models/node';
import { NodeAction } from '../models/node-action';
import { ActionParameter } from '../models/action-parameter';

export class NodeComparator {
  public static nodesDifferent(node1: Node, node2: Node): boolean {
//     console.log('nodes the same: ' + (node1 === node2 || JSON.stringify(node1) === JSON.stringify(node2)));
    if (node1 === node2 || JSON.stringify(node1) === JSON.stringify(node2)) {
      return false;
    }
//     console.log('node rooms different: ' + (node1.room !== node2.room));
    let diff: boolean = node1.room !== node2.room;
//     console.log('node names different: ' + (node1.name !== node2.name));
    diff ||= node1.name !== node2.name;
    return diff || NodeComparator.nodeActionArraysDifferent(node1.publicActions, node2.publicActions)
  }

  public static nodeActionArraysDifferent(actions1: NodeAction[], actions2: NodeAction[]): boolean {
//     console.log('node action arrays the same: ' + (actions1 === actions2 || JSON.stringify(actions1) === JSON.stringify(actions2)));
    if (actions1 === actions2 || JSON.stringify(actions1) === JSON.stringify(actions2)) {
      return false;
    }
//     console.log('node action array lengths different: ' + (actions1.length !== actions2.length));
    if (actions1.length !== actions2.length) {
      return true;
    }
    // lengths are the same; can safely loop through one's length and not AIOOBE on the other
    let pairings: Map<NodeAction, NodeAction> = new Map();
    let anyDiff: boolean = false;
    for (let i: number = 0; i < actions1.length; i++) {
      for (let j: number = 0; j < actions2.length; j++) {
        if (actions1[i].actionSeq === actions2[j].actionSeq) {
          pairings.set(actions1[i], actions2[j]);
        }
      }
    }
//     console.log('node action pairings for comparison: ' + JSON.stringify(pairings));
    for (let [key, value] of pairings) {
      anyDiff ||= NodeComparator.nodeActionsDifferent(key, value);
    }
    return anyDiff;
  }

  public static nodeActionsDifferent(action1: NodeAction, action2: NodeAction): boolean {
//     console.log('actions the same: ' + (action1 === action2));
    if (action1 === action2) {
      return false;
    }
//     console.log('action names the same: ' + (action1.name !== action2.name));
    let diff: boolean = action1.name !== action2.name;
//     console.log('action handlers the same: ' + (action1.handler !== action2.handler));
    diff ||= action1.handler !== action2.handler;
    return diff ||= NodeComparator.nodeActionParameterArraysDifferent(action1.parameters, action2.parameters);
  }

  public static nodeActionParameterArraysDifferent(params1: ActionParameter[], params2: ActionParameter[]): boolean {
//     console.log('node action parameter arrays the same: ' + (params1 === params2));
    if (params1 === params2) {
      return false;
    }
//     console.log('node action parameter array lengths the same: ' + (params1.length !== params2.length));
    if (params1.length !== params2.length) {
      return true;
    }
//     console.log('nothing is different');
    return false;
  }
}
