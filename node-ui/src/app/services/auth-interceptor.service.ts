import { Injectable } from '@angular/core';
import { HttpRequest, HttpHandler, HttpEvent, HttpInterceptor } from '@angular/common/http';

import { Observable } from 'rxjs';

import { UserProviderService } from './user-provider.service';

@Injectable({
  providedIn: 'root'
})
export class AuthInterceptorService implements HttpInterceptor {
  constructor(private userProvider: UserProviderService) { }

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const jwt = this.userProvider.getJwt();
    console.log('intercepting and setting authorization header');
    if (jwt && !this.isJwtExpired(jwt)) {
      request = request.clone({
        setHeaders: {
          'Authorization': 'Bearer ' + jwt
        }
      });
    }
    return next.handle(request);
  }

  isJwtExpired(jwt: string): boolean {
    const expiry = (JSON.parse(atob(jwt.split('.')[1]))).exp;
    console.log((Math.floor((new Date).getTime() / 1000)) + '>=' + expiry);
    return (Math.floor((new Date).getTime() / 1000)) >= expiry;
  }
}
