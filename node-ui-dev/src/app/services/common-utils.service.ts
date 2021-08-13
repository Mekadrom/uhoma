import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class CommonUtilsService {
  constructor() { }

  omitSpecialChar(event: any): boolean {
    let k = event.charCode;
    return (k > 64 && k < 91) || (k > 96 && k < 123) || k == 8 || k == 32 || (k >= 48 && k <= 57);
  }

  onlyNumeric(event: any) {
    let k = event.charCode;
    return k >= 48 && k <= 57;
  }

  isStringField(typeDef: any): boolean {
    if (typeDef === null || typeDef === undefined) {
      return false;
    }
    return JSON.parse(typeDef.replace(/\\"/g, '"')).type === 'string';
  }

  isNumberField(typeDef: any): boolean {
    if (typeDef === null || typeDef === undefined) {
      return false;
    }
    return JSON.parse(typeDef.replace(/\\"/g, '"')).type === 'number';
  }

  isListField(typeDef: any): boolean {
    if (typeDef === null || typeDef === undefined) {
      return false;
    }
    return JSON.parse(typeDef.replace(/\\"/g, '"')).type === 'list';
  }

  getListValues(typeDef: any): string[] {
    if (typeDef === null || typeDef === undefined) {
      return [];
    }
    return JSON.parse(typeDef.replace(/\\"/g, '"')).values;
  }

  listValueAllowsEmpty(typeDef: any) {
    if (typeDef === null || typeDef === undefined) {
      return false;
    }
    const config: any = JSON.parse(typeDef.replace(/\\"/g, '"')).config;
    if (config) {
      return config.allowsEmpty;
    }
    return true;
  }

  consumeEvent(event: any): void {
    event?.stopPropagation();
  }
}
