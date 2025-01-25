import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IArea, NewArea } from '../area.model';

export type PartialUpdateArea = Partial<IArea> & Pick<IArea, 'id'>;

type RestOf<T extends IArea | NewArea> = Omit<T, 'dataCriacao' | 'dataInatividade'> & {
  dataCriacao?: string | null;
  dataInatividade?: string | null;
};

export type RestArea = RestOf<IArea>;

export type NewRestArea = RestOf<NewArea>;

export type PartialUpdateRestArea = RestOf<PartialUpdateArea>;

export type EntityResponseType = HttpResponse<IArea>;
export type EntityArrayResponseType = HttpResponse<IArea[]>;

@Injectable({ providedIn: 'root' })
export class AreaService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/areas');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(area: NewArea): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(area);
    return this.http.post<RestArea>(this.resourceUrl, copy, { observe: 'response' }).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(area: IArea): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(area);
    return this.http
      .put<RestArea>(`${this.resourceUrl}/${this.getAreaIdentifier(area)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(area: PartialUpdateArea): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(area);
    return this.http
      .patch<RestArea>(`${this.resourceUrl}/${this.getAreaIdentifier(area)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestArea>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestArea[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getAreaIdentifier(area: Pick<IArea, 'id'>): number {
    return area.id;
  }

  compareArea(o1: Pick<IArea, 'id'> | null, o2: Pick<IArea, 'id'> | null): boolean {
    return o1 && o2 ? this.getAreaIdentifier(o1) === this.getAreaIdentifier(o2) : o1 === o2;
  }

  addAreaToCollectionIfMissing<Type extends Pick<IArea, 'id'>>(
    areaCollection: Type[],
    ...areasToCheck: (Type | null | undefined)[]
  ): Type[] {
    const areas: Type[] = areasToCheck.filter(isPresent);
    if (areas.length > 0) {
      const areaCollectionIdentifiers = areaCollection.map(areaItem => this.getAreaIdentifier(areaItem)!);
      const areasToAdd = areas.filter(areaItem => {
        const areaIdentifier = this.getAreaIdentifier(areaItem);
        if (areaCollectionIdentifiers.includes(areaIdentifier)) {
          return false;
        }
        areaCollectionIdentifiers.push(areaIdentifier);
        return true;
      });
      return [...areasToAdd, ...areaCollection];
    }
    return areaCollection;
  }

  protected convertDateFromClient<T extends IArea | NewArea | PartialUpdateArea>(area: T): RestOf<T> {
    return {
      ...area,
      dataCriacao: area.dataCriacao?.toJSON() ?? null,
      dataInatividade: area.dataInatividade?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restArea: RestArea): IArea {
    return {
      ...restArea,
      dataCriacao: restArea.dataCriacao ? dayjs(restArea.dataCriacao) : undefined,
      dataInatividade: restArea.dataInatividade ? dayjs(restArea.dataInatividade) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestArea>): HttpResponse<IArea> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestArea[]>): HttpResponse<IArea[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
