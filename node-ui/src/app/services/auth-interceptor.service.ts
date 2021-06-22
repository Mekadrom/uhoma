import { Injectable } from '@angular/core';
import { HttpRequest, HttpHandler, HttpEvent, HttpInterceptor } from '@angular/common/http';

import { Observable } from 'rxjs';

import { UserProviderService } from './user-provider.service';

@Injectable({
  providedIn: 'root'
})
export class AuthInterceptorService implements HttpInterceptor {
  constructor(private userProvider: UserProviderService) { }

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const jwt = this.userProvider.getJwt();
    if (jwt) {
      req = req.clone({
        setHeaders: {
          'Authorization': 'Bearer ' + jwt
        }
      });
    }
    req = req.clone({
      setHeaders: {
      }
    });
    return next.handle(req);
  }
}
