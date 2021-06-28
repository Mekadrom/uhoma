import { NodeAction } from './node-action';

export interface ActionParameter {
  actionParameterSeq: number;
  currentValue?: string;
  defaultValue?: string;
  type: string;
  name: string;
  action: NodeAction;
}
