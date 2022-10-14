import { AfterViewInit, Component } from '@angular/core';
import { CookieService } from 'ngx-cookie-service';

import { UserView } from '../models';
import { AuthService, UserProviderService, WebSocketService } from '../services';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements AfterViewInit {
  constructor(private authService: AuthService,
              private cookieService: CookieService,
              private userProviderService: UserProviderService,
              private webSocketService: WebSocketService) { }

  ngAfterViewInit(): void {
    const jwt: string | null | undefined = this.cookieService.get('bearer');
    if (!this.userProviderService.getUserView()) {
      this.authService.refreshUserView();
    }
    if (this.cookieService.get('bearer') && this.userProviderService.getUserView() && !this.webSocketService.isConnected()) {
      this.webSocketService.attach(this.cookieService.get('bearer'));
    }
  }
}
