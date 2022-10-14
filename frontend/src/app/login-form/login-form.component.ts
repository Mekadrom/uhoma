import { Component } from '@angular/core';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { FormBuilder, FormGroup, FormControl, Validators} from '@angular/forms';

import { ToastrService } from 'ngx-toastr';

import { UserView } from '../models';
import { AuthService, CommonUtilsService } from '../services';

@Component({
  selector: 'app-login-form',
  templateUrl: './login-form.component.html',
  styleUrls: ['./login-form.component.scss']
})
export class LoginFormComponent {
  public loginForm: FormGroup = new FormGroup({});

  username: string = '';
  password: string = '';

  constructor(private authService: AuthService,
              public commonUtilsService: CommonUtilsService,
              private fb: FormBuilder,
              private toastr: ToastrService) {
    this.loginForm = fb.group({
      username: ['', [Validators.required]],
      password: ['', [Validators.required]]
    });
  }

  login(): void {
    this.authService.login(this.username, this.password).subscribe(
      (resp: HttpResponse<UserView>) => {
        if (resp.headers.get('Authorization')) {
          this.toastr.success('Login successful, welcome ' + resp?.body?.username);
        } else {
          this.toastr.error('Login unsuccessful');
        }
      },
      (err: any) => {
        this.toastr.error(err.message, 'Login unsuccessful');
      }
    );
  }

  get loginFormControls(): any {
    return this.loginForm.controls;
  }

  attemptLogin(): void {
    if (this.loginForm.valid) {
      this.login();
    }
  }
}
