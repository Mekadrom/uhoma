import { Component, OnInit } from '@angular/core';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { ToastrService } from 'ngx-toastr';

import { UserView } from '../models';
import { AuthService, CommonUtilsService, WebSocketService, UserProviderService } from '../services';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {
  username: string = '';
  password: string = '';

  constructor(private authService: AuthService,
              public commonUtils: CommonUtilsService,
              private toastr: ToastrService,
              private userProvider: UserProviderService,
              private webSocket: WebSocketService) { }

  ngOnInit(): void {
  }

  login(): void {
    this.authService.login(this.username, this.password).subscribe(
      (resp: HttpResponse<UserView>) => {
        if (resp.headers.get('Authorization')) {
          this.toastr.success('Login successful, welcome ' + resp?.body?.username);
        } else {
          this.toastr.error('Login unsuccessful');
        }
        this.webSocket.attach(this.userProvider.getJwt());
      },
      (err: any) => {
        this.toastr.error(err.message, 'Login unsuccessful');
      }
    );
  }
}
