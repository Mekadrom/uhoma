import { Action, ActionParameterType } from '../models';

export interface ActionParameter {
  actionParameterSeq?: number;
  currentValue?: string;
  defaultValue?: string;
  actionParameterType?: ActionParameterType;
  actionParameterTypeSeq?: number;
  name?: string;
  action?: Action;
  rowNum?: number;
}
