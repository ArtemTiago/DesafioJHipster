import dayjs from 'dayjs/esm';
import { StatusCurso } from 'app/entities/enumerations/status-curso.model';

export interface IArea {
  id: number;
  nome?: string | null;
  descricao?: string | null;
  status?: StatusCurso | null;
  dataCriacao?: dayjs.Dayjs | null;
  dataInatividade?: dayjs.Dayjs | null;
}

export type NewArea = Omit<IArea, 'id'> & { id: null };
