import {EmitterSubscription} from 'react-native';

export enum Events {
    EXIT = 'onExit',
    ENTER = 'onEnter',
    ERROR = 'onError',
    DWELL = 'onDwell',
}

export interface Boundary {
    id: string;
    lat: number;
    lng: number;
    radius: number;
}

export interface BoundaryStatic {
    on: (event: Events, callback: (boundaries: string[]) => void) => EmitterSubscription;
    off: (event: Events) => void;
    add: (boundary: Boundary) => Promise<string>;
    onReRegisterRequired: (callback: () => void) => void;
    remove: (id: string) => Promise<null>;
    removeAll: () => Promise<null>;
}

declare let Boundary: BoundaryStatic;
export default Boundary;
