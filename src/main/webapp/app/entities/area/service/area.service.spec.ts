import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IArea } from '../area.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../area.test-samples';

import { AreaService, RestArea } from './area.service';

const requireRestSample: RestArea = {
  ...sampleWithRequiredData,
  dataCriacao: sampleWithRequiredData.dataCriacao?.toJSON(),
  dataInatividade: sampleWithRequiredData.dataInatividade?.toJSON(),
};

describe('Area Service', () => {
  let service: AreaService;
  let httpMock: HttpTestingController;
  let expectedResult: IArea | IArea[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(AreaService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a Area', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const area = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(area).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Area', () => {
      const area = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(area).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Area', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Area', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Area', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addAreaToCollectionIfMissing', () => {
      it('should add a Area to an empty array', () => {
        const area: IArea = sampleWithRequiredData;
        expectedResult = service.addAreaToCollectionIfMissing([], area);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(area);
      });

      it('should not add a Area to an array that contains it', () => {
        const area: IArea = sampleWithRequiredData;
        const areaCollection: IArea[] = [
          {
            ...area,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addAreaToCollectionIfMissing(areaCollection, area);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Area to an array that doesn't contain it", () => {
        const area: IArea = sampleWithRequiredData;
        const areaCollection: IArea[] = [sampleWithPartialData];
        expectedResult = service.addAreaToCollectionIfMissing(areaCollection, area);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(area);
      });

      it('should add only unique Area to an array', () => {
        const areaArray: IArea[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const areaCollection: IArea[] = [sampleWithRequiredData];
        expectedResult = service.addAreaToCollectionIfMissing(areaCollection, ...areaArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const area: IArea = sampleWithRequiredData;
        const area2: IArea = sampleWithPartialData;
        expectedResult = service.addAreaToCollectionIfMissing([], area, area2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(area);
        expect(expectedResult).toContain(area2);
      });

      it('should accept null and undefined values', () => {
        const area: IArea = sampleWithRequiredData;
        expectedResult = service.addAreaToCollectionIfMissing([], null, area, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(area);
      });

      it('should return initial array if no Area is added', () => {
        const areaCollection: IArea[] = [sampleWithRequiredData];
        expectedResult = service.addAreaToCollectionIfMissing(areaCollection, undefined, null);
        expect(expectedResult).toEqual(areaCollection);
      });
    });

    describe('compareArea', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareArea(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareArea(entity1, entity2);
        const compareResult2 = service.compareArea(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareArea(entity1, entity2);
        const compareResult2 = service.compareArea(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareArea(entity1, entity2);
        const compareResult2 = service.compareArea(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
