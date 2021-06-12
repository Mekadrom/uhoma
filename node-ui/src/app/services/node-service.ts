import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Node } from '../models/node';

@Injectable
export class NodeService {
  constructor(private http: HttpClient) {}

  get = {
    node: (nodeName: string): Promise<Node> => {
      return this.http.get("http://localhost:8080/node/search?name=" + nodeName).map(response => {
        return response.json();
      }).toPromise();
    }
  }
}
