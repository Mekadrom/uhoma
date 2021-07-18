import { Component, OnInit } from '@angular/core';

import { ToastrService } from 'ngx-toastr';

import { Validator } from '../validator/validator';
import { ListValidator } from '../validator/list-validator';
import { NoOpValidator } from '../validator/noop-validator';
import { NumberValidator } from '../validator/number-validator';
import { UrlProviderService } from '../services/url-provider.service';

export interface Setting {
  name: string;
  value: string;
  defaultValue: string;
  type: string;
  saveCallback: (value: string) => void;
  validator: Validator;
}

@Component({
  selector: 'app-settings',
  templateUrl: './settings.component.html',
  styleUrls: ['./settings.component.css']
})
export class SettingsComponent implements OnInit {
  displayedColumns: string[] = ['name', 'value'];

  settings:  Setting[] = [
    { name: 'HAMS Host Address', value: '', defaultValue: '', type: 'string', saveCallback: (value: string) => {}, validator: new NoOpValidator() },
    { name: 'HAMS Host Port', value: '', defaultValue: '', type: 'number', saveCallback: (value: string) => {}, validator: new NumberValidator() },
    { name: 'HAMS Web Socket Endpoint', value: '', defaultValue: '', type: 'string', saveCallback: (value: string) => {}, validator: new NoOpValidator() }
  ];

  constructor(private urlProvider: UrlProviderService,
              private toastr: ToastrService) { }

  ngOnInit(): void {
    this.getSettings();
  }

  reset(): void {
    this.getSettings();
  }

  getSettings(): void {
    this.initSetting(this.settings[0].name, this.urlProvider.getHamsHost(), this.urlProvider.setHamsHost);
    this.initSetting(this.settings[1].name, this.urlProvider.getHamsPort(), this.urlProvider.setHamsPort);
    this.initSetting(this.settings[2].name, this.urlProvider.getHamsWebSocketEndpoint(), this.urlProvider.setHamsWebSocketEndpoint);
  }

  initSetting(settingName: string, value: string, saveCallback: (value: string) => void): void {
    this.settings
      .filter(setting => setting.name.toUpperCase().indexOf(settingName.toUpperCase()) != -1)
      .forEach(setting => {
        if (setting.type !== 'list') {
          setting.value = value;
          setting.defaultValue = value;
        }
        setting.saveCallback = saveCallback;
      });
  }

  save(): void {
    const failures: Setting[] = this.settings.filter((setting) => setting.validator.validate(setting.value));
    if (failures.length > 0) {
      this.showError(failures);
    } else {
      this.settings.forEach(setting => setting.saveCallback(setting.value));
    }
  }

  showError(failures: Setting[]): void {
    this.toastr.error(failures.toString());
  }

  getListAndSetDefaultValue(setting: Setting): [string, string[]] {
    const defaultValueAndList: string[] = setting.defaultValue.split(';');
    if (defaultValueAndList && defaultValueAndList.length > 1) {
      const defaultValue: string = defaultValueAndList[0];
      const listString: string = defaultValueAndList[1];
      if (listString) {
        const listValues: string[] = listString.split(',');
        if (listValues) {
          setting.value = defaultValue;
          return [ defaultValue, listValues ];
        }
      }
    }
    return [ '', [] ]
  }
}
