import { Injectable } from '@angular/core';

import { UserView } from '../models';

@Injectable({
  providedIn: 'root'
})
export class UserProviderService {
  private userView?: UserView | null;

  constructor() { }

  getUserView(): UserView | null | undefined {
    return this.userView;
  }

  setUserView(userView: UserView | null | undefined) {
    this.userView = userView;
  }
}
