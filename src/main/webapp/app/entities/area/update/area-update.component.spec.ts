import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { AreaFormService } from './area-form.service';
import { AreaService } from '../service/area.service';
import { IArea } from '../area.model';

import { AreaUpdateComponent } from './area-update.component';

describe('Area Management Update Component', () => {
  let comp: AreaUpdateComponent;
  let fixture: ComponentFixture<AreaUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let areaFormService: AreaFormService;
  let areaService: AreaService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [AreaUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(AreaUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(AreaUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    areaFormService = TestBed.inject(AreaFormService);
    areaService = TestBed.inject(AreaService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const area: IArea = { id: 456 };

      activatedRoute.data = of({ area });
      comp.ngOnInit();

      expect(comp.area).toEqual(area);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IArea>>();
      const area = { id: 123 };
      jest.spyOn(areaFormService, 'getArea').mockReturnValue(area);
      jest.spyOn(areaService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ area });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: area }));
      saveSubject.complete();

      // THEN
      expect(areaFormService.getArea).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(areaService.update).toHaveBeenCalledWith(expect.objectContaining(area));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IArea>>();
      const area = { id: 123 };
      jest.spyOn(areaFormService, 'getArea').mockReturnValue({ id: null });
      jest.spyOn(areaService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ area: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: area }));
      saveSubject.complete();

      // THEN
      expect(areaFormService.getArea).toHaveBeenCalled();
      expect(areaService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IArea>>();
      const area = { id: 123 };
      jest.spyOn(areaService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ area });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(areaService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
