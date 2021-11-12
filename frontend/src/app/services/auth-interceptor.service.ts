import { Injectable } from '@angular/core';
import { HttpRequest, HttpHandler, HttpEvent, HttpInterceptor, HttpResponse } from '@angular/common/http';
import { CookieService } from 'ngx-cookie-service';
import { Observable } from 'rxjs';
import 'rxjs/add/operator/map';

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
    request = this.setTokenHeader(request, 'Authorization', jwt);
    request = request.clone({
      headers: request.headers.append('Access-Control-Allow-Origin', '*')
    });
    return next.handle(request);
  }

  setTokenHeader(request: HttpRequest<any>, headerName: string, token: string | null | undefined): HttpRequest<any> {
    if (token && !this.authService.isJwtExpired(token)) {
      return request.clone({
        headers: request.headers.append(headerName, 'Bearer ' + token)
      });
    }
    return request;
  }
}
