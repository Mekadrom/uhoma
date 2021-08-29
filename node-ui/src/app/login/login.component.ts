import { AfterViewInit, Component } from '@angular/core';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { CookieService } from 'ngx-cookie-service';
import { ToastrService } from 'ngx-toastr';

import { UserView } from '../models';
import { ActionHandlerService, ActionParameterTypeService, AuthService, CommonUtilsService, NodeService, RoomService, UserProviderService, WebSocketService } from '../services';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements AfterViewInit {
  username: string = '';
  password: string = '';

  constructor(private actionHandlerService: ActionHandlerService,
              private actionParameterTypeService: ActionParameterTypeService,
              private authService: AuthService,
              public commonUtilsService: CommonUtilsService,
              private cookieService: CookieService,
              private nodeService: NodeService,
              private roomService: RoomService,
              private toastr: ToastrService,
              private userProviderService: UserProviderService,
              private webSocketService: WebSocketService) { }

  ngAfterViewInit(): void {
    if (this.authService.isJwtExpired(this.cookieService.get('bearer'))) {
      this.reauth();
    }
    if (!this.userProviderService.getUserView()) {
      this.authService.refreshUserView();
    }
    if (this.cookieService.get('bearer') && this.userProviderService.getUserView() && !this.webSocketService.isConnected()) {
      this.webSocketService.attach(this.cookieService.get('bearer'));
    }
  }

  reauth(): void {
    const refreshToken: string = this.cookieService.get('refresh');
    if(refreshToken && !this.authService.isJwtExpired(refreshToken)) {
      this.authService.refreshJwt(refreshToken);
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
}
