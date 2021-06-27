import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpResponse } from '@angular/common/http';

import { Observable, throwError } from 'rxjs';
import { retry, catchError, shareReplay } from 'rxjs/operators';
import { CookieService } from 'ngx-cookie-service';

import { UserView } from '../models/user-view';
import { UrlProviderService } from './url-provider.service';
import { UserProviderService } from './user-provider.service';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  constructor(private http: HttpClient,
              private urlProvider: UrlProviderService,
              private userProvider: UserProviderService,
              private cookieService: CookieService) { }

  public login(username: string, password: string): Observable<HttpResponse<UserView>> {
    const obs = this.http.post<UserView>(this.getAuthUrl(), { username, password }, { observe: 'response' })
    .pipe(
      retry(1),
      catchError(this.handleError),
      shareReplay(1)
    );
    obs.subscribe(
      (resp: HttpResponse<UserView>) => {
        this.processResponse(resp);
      }
    );
    return obs;
  }

  private processResponse(resp: HttpResponse<any>): void {
    const jwt = resp.headers.get('Authorization');
    if (jwt) {
      this.userProvider.setJwt(jwt);
      this.userProvider.setUserView(resp.body);
      this.cookieService.set('bearer', jwt);
    }
  }

  private getAuthUrl() {
    return this.urlProvider.getAuthUrl();
  }

  private handleError(error: any): Observable<any> {
    return throwError(error);
  }
}
