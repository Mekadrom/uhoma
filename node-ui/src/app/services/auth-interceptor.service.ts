import { Injectable } from '@angular/core';
import { HttpRequest, HttpHandler, HttpEvent, HttpInterceptor } from '@angular/common/http';
import { CookieService } from 'ngx-cookie-service';
import { Observable } from 'rxjs';

import { AuthService, UserProviderService } from '../services';

@Injectable({
  providedIn: 'root'
})
export class AuthInterceptorService implements HttpInterceptor {
  constructor(private authService: AuthService,
              private userProviderService: UserProviderService,
              private cookieService: CookieService) { }

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const jwt = this.cookieService.get('bearer');
    if (jwt && !this.authService.isJwtExpired(jwt)) {
      request = request.clone({
        setHeaders: {
          'Authorization': 'Bearer ' + jwt
        }
      });
    }
    request = request.clone({
      setHeaders: {
        'Access-Control-Allow-Origin': '*'
      }
    });
    return next.handle(request);
  }
}
