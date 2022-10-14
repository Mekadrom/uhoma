import { Component } from '@angular/core';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { FormBuilder, FormGroup, FormControl, Validators} from '@angular/forms';

import { ToastrService } from 'ngx-toastr';

import { ConfirmedValidator } from '../validators/confirmed.validator';
import { UserView } from '../models';
import { AuthService, CommonUtilsService } from '../services';

@Component({
  selector: 'app-register-form',
  templateUrl: './register-form.component.html',
  styleUrls: ['./register-form.component.scss']
})
export class RegisterFormComponent {
  public registerForm: FormGroup = new FormGroup({});

  usernameRegister: string = '';
  passwordRegister: string = '';
  passwordRegisterConfirm: string = '';
  constructor(private authService: AuthService,
              public commonUtilsService: CommonUtilsService,
              private fb: FormBuilder,
              private toastr: ToastrService) {
    this.registerForm = fb.group({
      passwordRegister: ['', [Validators.required]],
      passwordRegisterConfirm: ['', [Validators.required]]
    }, {
      validator: ConfirmedValidator('passwordRegister', 'passwordRegisterConfirm')
    });
  }

  register(): void {
    this.authService.register(this.usernameRegister, this.passwordRegister).subscribe(
      (resp: HttpResponse<UserView>) => {
        if (resp.headers.get('Authorization')) {
          this.toastr.success('Registration successful, welcome ' + resp?.body?.username);
        } else {
          this.toastr.error('Registration unsuccessful');
        }
      },
      (err: any) => {
        this.toastr.error(err.message, 'Registration unsuccessful');
      }
    );
  }

  get registerFormControls(): any {
    return this.registerForm.controls;
  }

  attemptRegister(): void {
    if (this.registerForm.valid) {
      this.register();
    }
  }
}
