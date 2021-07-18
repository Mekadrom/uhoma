import { Validator } from './validator';

export class NumberValidator implements Validator {
  constructor() { }

  validate(value: string): boolean {
    return !isNaN(+value);
  }
}
