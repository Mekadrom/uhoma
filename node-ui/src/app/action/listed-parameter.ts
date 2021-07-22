import { ActionParameter } from '../models/action-parameter';

export interface ListedParameter {
  rowNum?: number;
  currentValue?: string;
  actionParameter: ActionParameter;
}
