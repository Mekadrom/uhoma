import { Component } from '@angular/core';
import { Blade } from './enum-blade';
import { NodeComponent } from './node/node.component';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
})
export class AppComponent {
  private activeBlade: Blade = Blade.HOME;

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

  get isAdminBlade() {
    return this.activeBlade == Blade.ADMIN;
  }

  setAdminBlade(): void {
    this.activeBlade = Blade.ADMIN;
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
}
