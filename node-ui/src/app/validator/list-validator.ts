import { Validator } from './validator';

export class ListValidator implements Validator {
  constructor(private list: string[]) { }

  validate(value: string): boolean {
    return this.list.includes(value);
  }
}
