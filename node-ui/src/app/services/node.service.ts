import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Node } from '../models/node';
import { Observable, throwError } from 'rxjs';
import { Injectable } from '@angular/core';
import { retry, catchError } from 'rxjs/operators';
import { HttpResponse } from "@angular/common/http";
import { ToastrService } from 'ngx-toastr';

@Injectable({
  providedIn: 'root'
})
export class NodeService {
  constructor(private http: HttpClient, private toastr: ToastrService) {}

  getNodes(): Observable<Node[]> {
    return this.http.get<Node[]>(this.getNodeSearchUrl())
    .pipe(
      retry(1),
      catchError(this.handleError)
    )
  }

  getNodeSearchUrl(): string {
    var searchUrl: string = 'http://localhost:8080/node/search';

    console.log(searchUrl)
    return searchUrl;
  }

  handleError(error: any): Observable<any> {
    return throwError(error);
  }
}
