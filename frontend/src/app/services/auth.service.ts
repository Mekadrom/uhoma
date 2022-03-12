import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpResponse } from '@angular/common/http';
import { CookieService } from 'ngx-cookie-service';
import { Observable, throwError } from 'rxjs';
import { retry, catchError, shareReplay } from 'rxjs/operators';

import { UserView } from '../models';
import { UrlProviderService, UserProviderService } from '../services';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  constructor(private http: HttpClient,
              private urlProviderService: UrlProviderService,
              private userProviderService: UserProviderService,
              private cookieService: CookieService) { }

  public login(username: string, password: string): Observable<HttpResponse<UserView>> {
    this.logout();
    const obs = this.http.post<UserView>(this.urlProviderService.getLoginUrl(), { username, password }, { observe: 'response' }).pipe(
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

  public register(username: string, password: string): Observable<HttpResponse<UserView>> {
    this.logout();
    const obs = this.http.post<UserView>(this.urlProviderService.getRegistrationUrl(), { username, password }, { observe: 'response' }).pipe(
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

  public refreshUserView(): Observable<HttpResponse<UserView>> | null {
    const jwt: string | null | undefined = this.cookieService.get('bearer');
    if (jwt) {
      const obs: any = this.http.post<UserView>(this.urlProviderService.getUserViewRefreshUrl(), { }, { observe: 'response' }).pipe(
        retry(1),
        catchError(this.handleError),
        shareReplay(1)
      );
      obs.subscribe(
        (resp: HttpResponse<UserView>) => {
          this.userProviderService.setUserView(resp.body);
        }
      );
      return obs;
    }
    return null;
  }

  private clearBearer(): void {
    this.cookieService.delete('bearer');
  }

  public logout(): void {
    this.clearBearer();
    this.userProviderService.clear();
  }

  private processResponse(resp: HttpResponse<any>): void {
    const jwt: string | null | undefined = resp.headers.get('Authorization');
    if (jwt) {
      this.cookieService.set('bearer', jwt);
    }
    this.userProviderService.setUserView(resp.body as UserView);
  }

  private handleError(error: any): Observable<any> {
    return throwError(error);
  }

  isJwtExpired(jwt: string): boolean {
    const expiry = (JSON.parse(atob(jwt.split('.')[1]))).exp;
    return (Math.floor((new Date).getTime() / 1000)) >= expiry;
  }
}
