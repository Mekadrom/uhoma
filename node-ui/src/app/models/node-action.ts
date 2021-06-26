import { ActionParameter } from './action-parameter';

export interface NodeAction {
  actionSeq: number;
  name: string;
  ownerNodeSeq: number;
  handler: string;
  parameters: ActionParameter[];
}
