import { NodeAction } from './node-action';
import { ActionParameterType } from './action-parameter-type';

export interface ActionParameter {
  actionParameterSeq?: number;
  currentValue?: string;
  defaultValue?: string;
  actionParameterType?: ActionParameterType;
  actionParameterTypeSeq?: number;
  name?: string;
  action?: NodeAction;
  rowNum?: number;
}
