import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Node } from '../models/node';
import { Injectable } from '@angular/core';
import { Observable, throwError } from 'rxjs';
import { retry, catchError } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class NodeService {
  constructor(private http: HttpClient) {}

  getNodes(nodeName: string | null): Observable<Node[]> {
    return this.http.get<Node[]>(this.getNodeSearchUrl(nodeName))
    .pipe(
      retry(1),
      catchError(this.handleError)
    )
  }

  getNodeSearchUrl(nodeName: string | null): string {
    if (nodeName == null) {
    console.log('null search criteria')
      return 'http://localhost:8080/node/search'
    } else {
      return 'http://localhost:8080/node/search?name=' + nodeName
    }
  }

  handleError(error: any) {
    let errorMessage = '';
    if(error.error instanceof ErrorEvent) {
      // Get client-side error
      errorMessage = error.error.message;
    } else {
      // Get server-side error
      errorMessage = `Error Code: ${error.status}\nMessage: ${error.message}`;
    }
    window.alert(errorMessage);
    return throwError(errorMessage);
  }
}
