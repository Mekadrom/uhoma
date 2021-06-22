import { Injectable } from '@angular/core';

import { UserView } from '../models/user-view';

@Injectable({
  providedIn: 'root'
})
export class UserProviderService {
  private jwt?: string | null;
  private userView?: UserView | null;

  constructor() { }

  getJwt(): string | null | undefined {
    return this.jwt;
  }

  setJwt(jwt: string | null | undefined) {
    this.jwt = jwt;
  }

  getUserView(): UserView | null | undefined {
    return this.userView;
  }

  setUserView(userView: UserView | null | undefined) {
    this.userView = userView;
  }
}
