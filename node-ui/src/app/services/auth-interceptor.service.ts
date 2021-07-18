import { Injectable } from '@angular/core';
import { HttpRequest, HttpHandler, HttpEvent, HttpInterceptor } from '@angular/common/http';

import { CookieService } from 'ngx-cookie-service';

import { Observable } from 'rxjs';

import { UserProviderService } from './user-provider.service';

@Injectable({
  providedIn: 'root'
})
export class AuthInterceptorService implements HttpInterceptor {
  constructor(private userProvider: UserProviderService, private cookieService: CookieService) { }

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const jwt = this.cookieService.get('bearer');
    if (jwt && !this.isJwtExpired(jwt)) {
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

  isJwtExpired(jwt: string): boolean {
    const expiry = (JSON.parse(atob(jwt.split('.')[1]))).exp;
    return (Math.floor((new Date).getTime() / 1000)) >= expiry;
  }
}
