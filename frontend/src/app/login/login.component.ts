import { AfterViewInit, Component } from '@angular/core';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { FormBuilder, FormGroup, FormControl, Validators} from '@angular/forms';
import { CookieService } from 'ngx-cookie-service';
import { ToastrService } from 'ngx-toastr';

import { ConfirmedValidator } from './confirmed.validator';
import { UserView } from '../models';
import { ActionHandlerService, ActionParameterTypeService, AuthService, CommonUtilsService, NodeService, RoomService, UserProviderService, WebSocketService } from '../services';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements AfterViewInit {
  public form: FormGroup = new FormGroup({});

  username: string = '';
  password: string = '';

  usernameRegister: string = '';
  passwordRegister: string = '';
  passwordRegisterConfirm: string = '';

  constructor(private actionHandlerService: ActionHandlerService,
              private actionParameterTypeService: ActionParameterTypeService,
              private authService: AuthService,
              public commonUtilsService: CommonUtilsService,
              private cookieService: CookieService,
              private nodeService: NodeService,
              private roomService: RoomService,
              private fb: FormBuilder,
              private toastr: ToastrService,
              private userProviderService: UserProviderService,
              private webSocketService: WebSocketService) {
    this.form = fb.group({
      passwordRegister: ['', [Validators.required]],
      passwordRegisterConfirm: ['', [Validators.required]]
    }, {
      validator: ConfirmedValidator('passwordRegister', 'passwordRegisterConfirm')
    });
  }

  ngAfterViewInit(): void {
    const jwt: string | null | undefined = this.cookieService.get('bearer');
    if (!this.userProviderService.getUserView()) {
      this.authService.refreshUserView();
    }
    if (this.cookieService.get('bearer') && this.userProviderService.getUserView() && !this.webSocketService.isConnected()) {
      this.webSocketService.attach(this.cookieService.get('bearer'));
    }
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

  get formControls(): any {
    return this.form.controls;
  }
}
