import { Component, AfterViewInit } from '@angular/core';

import { CookieService } from 'ngx-cookie-service';

import { Blade } from './enum-blade';
import { NodeComponent } from './node/node.component';
import { UserProviderService } from './services/user-provider.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
})
export class AppComponent implements AfterViewInit {
  private activeBlade: Blade = Blade.HOME;

  constructor(private cookieService: CookieService, private userProvider: UserProviderService) { }

  setBlade(blade: Blade): void {
    this.activeBlade = blade;
  }

  get isHomeBlade() {
    return this.activeBlade == Blade.HOME;
  }

  setHomeBlade(): void {
    this.activeBlade = Blade.HOME;
  }

  get isNodeBlade() {
    return this.activeBlade == Blade.NODE;
  }

  setNodeBlade(): void {
    this.activeBlade = Blade.NODE;
  }

  get isDevBlade() {
    return this.activeBlade == Blade.DEV;
  }

  setDevBlade(): void {
    this.activeBlade = Blade.DEV;
  }

  get isSettingsBlade() {
    return this.activeBlade == Blade.SETTINGS;
  }

  setSettingsBlade(): void {
    this.activeBlade = Blade.SETTINGS;
  }

  ngAfterViewInit(): void {
    const jwt = this.cookieService.get('bearer');
    if (jwt) {
      this.userProvider.setJwt(jwt);
    }
  }
}
