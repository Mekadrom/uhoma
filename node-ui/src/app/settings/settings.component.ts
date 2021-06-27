import { Component, OnInit } from '@angular/core';

import { UrlProviderService } from '../services/url-provider.service';

export interface Setting {
  name: string;
  value: string;
  defaultValue: string;
  type: string;
  saveCallback: (value: string) => void;
}

@Component({
  selector: 'app-settings',
  templateUrl: './settings.component.html',
  styleUrls: ['./settings.component.css']
})
export class SettingsComponent implements OnInit {
  displayedColumns: string[] = ['name', 'value'];

  settings:  Setting[] = [
    { name: 'HAMS Url', value: '', defaultValue: '', type: 'string', saveCallback: (value: string) => {} }
  ];

  constructor(private urlProvider: UrlProviderService) { }

  ngOnInit(): void {
    this.getSettings();
  }

  reset(): void {
    this.getSettings();
  }

  getSettings(): void {
    this.initSetting('HAMS Url', this.urlProvider.getHamsUrl(), this.urlProvider.setHamsUrl);
  }

  initSetting(settingName: string, value: string, saveCallback: (value: string) => void): void {
    this.settings
      .filter(setting => setting.name.toUpperCase().indexOf(settingName.toUpperCase()) != -1)
      .forEach(setting => {
        setting.value = value;
        setting.defaultValue = value;
        setting.saveCallback = saveCallback;
      });
  }

  save(): void {
    this.settings.forEach(setting => setting.saveCallback(setting.value));
  }

  isSettingTextField(setting: Setting): boolean {
    return setting.type === 'string';
  }
}
