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
    const obs = this.http.post<UserView>(this.urlProviderService.getAuthUrl(), { username, password }, { observe: 'response' })
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
      this.userProviderService.setJwt(jwt);
      this.userProviderService.setUserView(resp.body);
      this.cookieService.set('bearer', jwt);
    }
  }

  private handleError(error: any): Observable<any> {
    return throwError(error);
  }
}
