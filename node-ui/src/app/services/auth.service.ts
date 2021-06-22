import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpResponse } from '@angular/common/http';

import { Observable, throwError } from 'rxjs';
import { retry, catchError, shareReplay } from 'rxjs/operators';

import { UserView } from '../models/user-view';
import { UrlProviderService } from './url-provider.service';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  constructor(private http: HttpClient, private urlProvider: UrlProviderService) { }

  public login(username: string, password: string): Observable<HttpResponse<UserView>> {
    return this.http.post<UserView>(this.getAuthUrl(), { username, password }, { observe: 'response' })
    .pipe(
      retry(1),
      catchError(this.handleError),
      shareReplay(1)
    );
  }

  private getAuthUrl() {
    return this.urlProvider.getAuthUrl();
  }

  private handleError(error: any): Observable<any> {
    return throwError(error);
  }
}
