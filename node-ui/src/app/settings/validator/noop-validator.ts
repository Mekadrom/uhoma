import { Validator } from './validator';

export class NoOpValidator implements Validator {
  constructor() { }

  validate(value: string): boolean {
    return true;
  }
}
