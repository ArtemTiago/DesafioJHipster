import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../area.test-samples';

import { AreaFormService } from './area-form.service';

describe('Area Form Service', () => {
  let service: AreaFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AreaFormService);
  });

  describe('Service methods', () => {
    describe('createAreaFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createAreaFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            nome: expect.any(Object),
            descricao: expect.any(Object),
            status: expect.any(Object),
            dataCriacao: expect.any(Object),
            dataInatividade: expect.any(Object),
          })
        );
      });

      it('passing IArea should create a new form with FormGroup', () => {
        const formGroup = service.createAreaFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            nome: expect.any(Object),
            descricao: expect.any(Object),
            status: expect.any(Object),
            dataCriacao: expect.any(Object),
            dataInatividade: expect.any(Object),
          })
        );
      });
    });

    describe('getArea', () => {
      it('should return NewArea for default Area initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createAreaFormGroup(sampleWithNewData);

        const area = service.getArea(formGroup) as any;

        expect(area).toMatchObject(sampleWithNewData);
      });

      it('should return NewArea for empty Area initial value', () => {
        const formGroup = service.createAreaFormGroup();

        const area = service.getArea(formGroup) as any;

        expect(area).toMatchObject({});
      });

      it('should return IArea', () => {
        const formGroup = service.createAreaFormGroup(sampleWithRequiredData);

        const area = service.getArea(formGroup) as any;

        expect(area).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IArea should not enable id FormControl', () => {
        const formGroup = service.createAreaFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewArea should disable id FormControl', () => {
        const formGroup = service.createAreaFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
